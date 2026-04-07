package ru.astondevs.mycare.consumer.outbox;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.constants.MessageContractConstants.Headers.OUTBOX_ID;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.ERROR_MESSAGE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.OFFSET;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.PARTITION;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.REASON;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.STATUS;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.TOPIC;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Meters.ACK_FAILURE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Meters.EVENT_ACKNOWLEDGED;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.ErrorReasons.DB_INCONSISTENCY;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.ErrorReasons.MISSING_HEADER;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.ErrorReasons.UNEXPECTED;
import static ru.astondevs.mycare.util.kafka.ConsumerRecordHeaderUtils.extractHeaderAsUuid;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.astondevs.mycare.exception.kafka.MissingOutboxIdException;
import ru.astondevs.mycare.models.entity.OutboxEvent;
import ru.astondevs.mycare.models.enums.OutboxStatus;
import ru.astondevs.mycare.repository.OutboxRepository;

/**
 * Kafka-консьюмер, отвечающий за подтверждение
 * событий, отправленных через Outbox (Acknowledger).
 * <p>
 * Слушает собственные исходящие топики в пакетном (batch) режиме.
 * Его задача — найти ID события (который Debezium положил в заголовок)
 * и обновить его статус в БД на 'SENT'.
 * <p>
 * <b>Важно:</b> Реализация {@link #acknowledgeOutboxEvent}
 * идемпотентна и потокобезопасна,
 * что критически важно для кластерной среды (несколько инстансов)
 * и повторных доставок сообщений Кафкой.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventAcknowledger {

    /**
     * Репозиторий для доступа к таблице `outbox`
     * для обновления статуса события.
     */
    private final OutboxRepository outboxRepository;

    /**
     * Реестр метрик Micrometer для регистрации инцидентов.
     */
    private final MeterRegistry meterRegistry;

    /**
     * Слушает исходящие топики в пакетном режиме
     * для подтверждения доставки.
     * <p>
     * Вся пачка (batch) обрабатывается в одной транзакции.
     * Если хотя бы одно обновление в пачке падает
     * (и это не идемпотентная повторная доставка),
     * вся транзакция откатывается, и Kafka
     * повторит доставку всего батча.
     *
     * @param records Список (пачка) сообщений,
     * полученных от Kafka.
     * @throws MissingOutboxIdException если
     * заголовок 'outbox_id' отсутствует
     * или событие не найдено в БД
     * в ожидаемом состоянии.
     */
    @KafkaListener(
        topics = {
            "${app.kafka.topics.insurance-application-events}",
            "${app.kafka.topics.policy-events}"
        },
        groupId = "${starter.kafka.consumer.group-id}",
        containerFactory = "stringKafkaListenerContainerFactory"
    )
    @Transactional
    public void acknowledgeOutboxEvent(List<ConsumerRecord<String, Object>> records) {

        for (int i = 0; i < records.size(); i++) {
            ConsumerRecord<String, Object> record = records.get(i);

            try {
                processRecord(record);
            } catch (Exception e) {
                throw new BatchListenerFailedException(e.getMessage(), e, i);
            }
        }
    }

    /**
     * Выполняет атомарную обработку отдельной записи из полученного пакета Kafka.
     * <p>
     * Жизненный цикл обработки записи включает следующие этапы:
     * <ol>
     * <li> Извлечение {@code outbox_id} из заголовков сообщения.
     * При отсутствии заголовка вызывается процедура фатального сбоя.</li>
     * <li> Выполнение условного обновления в базе данных (статус PENDING -> SENT).</li>
     * <li> Если база данных подтвердила обновление, через
     * {@link TransactionSynchronizationManager} регистрируется обратный вызов. Метрика
     * успеха инкрементируется строго в методе {@code afterCommit}. Это гарантирует точность данных
     * в Grafana, исключая учет событий, транзакция по которым была откачена.</li>
     * <li>Если обновление не затронуло строк, запускается проверка
     * на идемпотентность (повторная доставка) или отсутствие записи.</li>
     * </ol>
     * <p>
     * Любое исключение, возникшее в процессе, логируется с использованием структурированных аргументов
     * и инкрементирует счетчик ошибок. Исключение пробрасывается выше для инициирования
     * отката всей транзакции пакета.
     *
     * @param record Объект {@link ConsumerRecord}, содержащий данные и метаданные сообщения.
     * @throws MissingOutboxIdException При нарушении контракта заголовков или бизнес-логики подтверждения.
     * @throws RuntimeException При системных сбоях (например, потеря соединения с СУБД).
     */
    private void processRecord(ConsumerRecord<String, Object> record) {
        UUID outboxId = null;
        try {
            outboxId = extractHeaderAsUuid(record, OUTBOX_ID);

            if (outboxId == null) {
                handleMissingHeader(record);
                return;
            }

            int updatedRows = outboxRepository.updateStatusToSent(outboxId, Instant.now());

            if (updatedRows > 0) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        meterRegistry.counter(
                            EVENT_ACKNOWLEDGED,
                            TOPIC, record.topic()
                        ).increment();
                    }
                });
            } else {
                handleUpdateZeroRows(outboxId, record);
            }

        } catch (MissingOutboxIdException e) {
            throw e;
        } catch (Exception e) {
            log.error("CRITICAL_ACKNOWLEDGMENT_FAILURE: Unhandled exception during acknowledgment. " +
                      "Kafka will retry the whole batch.",
                keyValue(TOPIC, record.topic()),
                keyValue(PARTITION, record.partition()),
                keyValue(OFFSET, record.offset()),
                keyValue(ERROR_MESSAGE, e.getMessage()),
                e
            );

            meterRegistry.counter(
                ACK_FAILURE,
                REASON,
                UNEXPECTED
            ).increment();

            throw e;
        }
    }

    /**
     * Обрабатывает критическую ситуацию отсутствия заголовка 'outbox_id'.
     * <p>
     * Отсутствие ID делает невозможным подтверждение события.
     * Метод регистрирует инцидент в логах как CRITICAL_FAILURE и инкрементирует метрику ошибок,
     * после чего прерывает обработку батча.
     * </p>
     *
     * @param record Сообщение Kafka, в котором отсутствует обязательный заголовок.
     * @throws MissingOutboxIdException Всегда выбрасывается для отката транзакции и отправки в DLT/Retry.
     */
    private void handleMissingHeader(ConsumerRecord<String, Object> record) {

        log.error("CRITICAL_ACKNOWLEDGMENT_FAILURE: Missing 'outbox_id' header. " +
                  "Failing batch to trigger DLT.",
            keyValue(TOPIC, record.topic()),
            keyValue(PARTITION, record.partition()),
            keyValue(OFFSET, record.offset())
        );

        meterRegistry.counter(
            ACK_FAILURE,
            REASON,
            MISSING_HEADER
        ).increment();

        throw new MissingOutboxIdException(
            "Missing 'outbox_id' header, cannot acknowledge outbox event."
        );
    }

    /**
     * Анализирует причину неудачного обновления статуса (0 обновленных строк).
     * <p>
     * Выполняет проверку состояния записи в БД (Select) для разграничения двух сценариев:
     * <ol>
     * <li><b>Идемпотентность:</b> Событие уже обработано другим консьюмером или в прошлом ретрае.
     * Это считается успехом (Warn).</li>
     * <li><b>Неконсистентность:</b> Событие отсутствует в БД или находится в некорректном статусе.
     * Это ошибка (Error).</li>
     * </ol>
     * </p>
     *
     * @param outboxId ID события из заголовка.
     * @param record   Оригинальное сообщение Kafka (для контекста логирования).
     * @throws MissingOutboxIdException Если выявлена логическая неконсистентность данных (событие не найдено).
     */
    private void handleUpdateZeroRows(UUID outboxId, ConsumerRecord<String, Object> record) {

        log.warn("Update returned 0 rows, checking for idempotent redelivery.",
            keyValue(OUTBOX_ID, outboxId),
            keyValue(TOPIC, record.topic())
        );

        Optional<OutboxEvent> retrievedOutboxEvent = outboxRepository.findById(outboxId);

        if (retrievedOutboxEvent.isPresent() &&
            retrievedOutboxEvent.get().getOutboxStatus() != OutboxStatus.PENDING) {

            log.warn("Idempotent redelivery of processed event. Acknowledgment skipped.",
                keyValue(OUTBOX_ID, outboxId),
                keyValue(STATUS, retrievedOutboxEvent.get().getOutboxStatus())
            );
        } else {
            String reason = retrievedOutboxEvent.isEmpty() ? "Event not found in outbox"
                : "Event is still PENDING, but update query failed";

            log.error("CRITICAL_ACKNOWLEDGMENT_FAILURE: Cannot acknowledge event.",
                keyValue(OUTBOX_ID, outboxId),
                keyValue(REASON, reason)
            );

            meterRegistry.counter(
                ACK_FAILURE,
                REASON,
                DB_INCONSISTENCY
            ).increment();

            throw new MissingOutboxIdException(
                "Failed to acknowledge event: " + outboxId + ". " + reason
            );
        }
    }
}
