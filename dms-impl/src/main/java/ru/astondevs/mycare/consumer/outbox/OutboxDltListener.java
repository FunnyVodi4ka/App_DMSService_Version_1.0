package ru.astondevs.mycare.consumer.outbox;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.constants.MessageContractConstants.Headers.OUTBOX_ID;
import static ru.astondevs.mycare.util.constants.MessageContractConstants.Infrastructure.DLT_EXCEPTION_MESSAGE;
import static ru.astondevs.mycare.util.constants.MessageContractConstants.Infrastructure.DLT_EXCEPTION_STACKTRACE;
import static ru.astondevs.mycare.util.constants.MessageContractConstants.Infrastructure.DLT_ORIGINAL_TOPIC;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.ERROR_TYPE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.OFFSET;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.PARTITION;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.REASON;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.TOPIC;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Meters.DLT_FAILURE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Meters.EVENT_ISOLATED;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.ErrorReasons.MISSING_HEADER;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.ErrorReasons.POISON_PILL;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.MAX_STACK_TRACE_LENGTH;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.UNKNOWN_VALUE;
import static ru.astondevs.mycare.util.kafka.ConsumerRecordHeaderUtils.extractHeaderAsString;
import static ru.astondevs.mycare.util.kafka.ConsumerRecordHeaderUtils.extractHeaderAsUuid;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;
import ru.astondevs.mycare.repository.OutboxRepository;

/**
 * Kafka-консьюмер, отвечающий за обработку
 * Dead Letter Topic (DLT) для Outbox.
 * <p>
 * Его задача — слушать DLT-топики,
 * извлекать "отравленные" сообщения,
 * помечать их в `outbox` как `FAILED`
 * и создавать метрики для мониторинга:
 * <ul>
 * <li><b>Isolated:</b> Штатная изоляция бизнес-ошибки.</li>
 * <li><b>Failure:</b> Критический сбой самого механизма DLT.</li>
 * </ul>
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxDltListener {

    /**
     * Репозиторий для доступа к таблице `outbox`
     * для обновления статуса на `FAILED`.
     */
    private final OutboxRepository outboxRepository;

    /**
     * Реестр метрик Micrometer для регистрации инцидентов.
     */
    private final MeterRegistry meterRegistry;

    /**
     * Слушает DLT-топики, определенные в 'app.kafka.topics'.
     * <p>
     * <b>Важно:</b> Этот метод не использует
     * `@Transactional` на уровне Spring.
     * Каждый апдейт выполняется в своей транзакции,
     * определенной в репозитории.
     *
     * @param records Список "битых" сообщений.
     */
    @KafkaListener(
        topics = {
            "${app.kafka.topics.insurance-application-events-dlt}",
            "${app.kafka.topics.policy-events-dlt}"
        },
        groupId = "dms-insurance-dlt-consumer",
        containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void handleDltBatch(List<ConsumerRecord<String, Object>> records) {

        for (ConsumerRecord<String, Object> record : records) {
            try {
                UUID outboxId = extractHeaderAsUuid(record, OUTBOX_ID);

                if (outboxId == null) {
                    handleSystemFailure(
                        record,
                        "Missing 'outbox_id' header",
                        MISSING_HEADER,
                        null
                    );
                    continue;
                }

                isolatePoisonPill(outboxId, record);

            } catch (BatchListenerFailedException e) {
                handleSystemFailure(
                    record,
                    "Unexpected DB error during isolation",
                    e.getClass().getSimpleName(),
                    e
                );
            }
        }
    }

    /**
     * Выполняет штатную изоляцию "отравленного" сообщения.
     * <ol>
     * <li>Извлекает детали ошибки из заголовков.</li>
     * <li>Обновляет статус в БД на FAILED.</li>
     * <li>Логирует инцидент.</li>
     * <li>Пишет бизнес-метрику.</li>
     * </ol>
     */
    private void isolatePoisonPill(UUID outboxId, ConsumerRecord<String, Object> record) {

        String originalTopic = extractHeaderAsString(record, DLT_ORIGINAL_TOPIC);
        String exceptionMessage = extractHeaderAsString(record, DLT_EXCEPTION_MESSAGE);
        String stackTrace = extractHeaderAsString(record, DLT_EXCEPTION_STACKTRACE);

        String safeStackTrace =
            (stackTrace != null && stackTrace.length() > MAX_STACK_TRACE_LENGTH) ?
            stackTrace.substring(0, MAX_STACK_TRACE_LENGTH) :
            stackTrace;

        String fullErrorMessage = "Topic: %s | Exception: %s | StackTrace: %s".formatted(
            originalTopic,
            exceptionMessage,
            safeStackTrace
        );

        outboxRepository.updateStatusToFailed(
            outboxId,
            fullErrorMessage,
            Instant.now()
        );

        log.error(
            "POISON_PILL_ISOLATED: Event marked as FAILED. Business intervention required.",
            keyValue(OUTBOX_ID, outboxId),
            keyValue(DLT_ORIGINAL_TOPIC, originalTopic),
            keyValue(DLT_EXCEPTION_MESSAGE, exceptionMessage)
        );

        meterRegistry.counter(
            EVENT_ISOLATED,
            List.of(
                Tag.of(TOPIC, record.topic()),
                Tag.of(DLT_ORIGINAL_TOPIC, originalTopic != null ? originalTopic : UNKNOWN_VALUE),
                Tag.of(ERROR_TYPE, POISON_PILL)
            )
        ).increment();
    }

    /**
     * Обрабатывает критический системный сбой самого DLT-листенера.
     * (Например, БД недоступна и мы не можем даже пометить сообщение как FAILED).
     */
    private void handleSystemFailure(
        ConsumerRecord<String, Object> record,
        String reason,
        String errorType,
        Throwable t
    ) {
        log.error(
            "CRITICAL_DLT_FAILURE: DLT mechanism failed. Manual intervention required.",
            keyValue(TOPIC, record.topic()),
            keyValue(PARTITION, record.partition()),
            keyValue(OFFSET, record.offset()),
            keyValue(REASON, reason),
            t
        );

        meterRegistry.counter(
            DLT_FAILURE,
            List.of(
                Tag.of(ERROR_TYPE, errorType),
                Tag.of(TOPIC, record.topic())
            )
        ).increment();
    }
}
