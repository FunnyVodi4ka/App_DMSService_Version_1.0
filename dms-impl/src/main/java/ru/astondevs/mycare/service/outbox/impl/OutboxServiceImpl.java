package ru.astondevs.mycare.service.outbox.impl;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.AGGREGATE_ID;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.AGGREGATE_TYPE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.CLIENT_ID;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.CORRELATION_ID;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.ERROR_MESSAGE;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.EVENT_ID;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.IP_ADDRESS;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.TOPIC;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.TRACEPARENT;
import static ru.astondevs.mycare.util.constants.ObservabilityConstants.Keys.USER_AGENT;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.exception.kafka.EventSerializationException;
import ru.astondevs.mycare.models.entity.OutboxEvent;
import ru.astondevs.mycare.models.enums.OutboxStatus;
import ru.astondevs.mycare.repository.OutboxRepository;
import ru.astondevs.mycare.service.outbox.OutboxService;

/**
 * Реализация {@link OutboxService}.
 * <p>
 * Обеспечивает транзакционное сохранение любых {@link DomainEvent} в виде
 * {@link OutboxEvent} для последующей асинхронной отправки через Debezium.
 * <p>
 * Также отвечает за ручной проброс контекста трассировки ({@link Tracer})
 * в поле `headers` для сквозного мониторинга (Zipkin, Kibana).
 *
 * @author Ivan Sergienko
 * @version 1.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    /**
     * Репозиторий для прямого доступа к таблице `outbox`.
     */
    private final OutboxRepository outboxRepository;

    /**
     * Jackson-маппер для сериализации DTO событий в JSON (payload).
     */
    private final ObjectMapper objectMapper;

    /**
     * Micrometer Tracer для доступа к текущему Span и Baggage (correlation-id).
     * Необходим для ручного проброса трассировки в Outbox.
     */
    private final Tracer tracer;

    /**
     * {@inheritDoc}
     * <p>
     * Этот метод должен вызываться внутри существующей транзакции
     * основного бизнес-метода для обеспечения атомарности.
     * <p>
     * В случае ошибки сериализации JSON выбрасывает {@link EventSerializationException},
     * что приводит к откату транзакции.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public <T extends DomainEvent> void createAndSaveEvent(
        String topic,
        UUID aggregateId,
        String aggregateType,
        T payload) {

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headersJson = buildTracingHeaders();

            OutboxEvent event = OutboxEvent
                .builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .topic(topic)
                .payload(payloadJson)
                .headers(headersJson)
                .outboxStatus(OutboxStatus.PENDING)
                .build();

            outboxRepository.save(event);

            log.info("Saved PENDING event to outbox",
                keyValue(EVENT_ID, payload.eventId()),
                keyValue(TOPIC, topic),
                keyValue(AGGREGATE_ID, aggregateId)
            );

        } catch (JsonProcessingException e) {
            log.error("CRITICAL: Failed to serialize event. Transaction will be rolled back.",
                keyValue(EVENT_ID, payload.eventId()),
                keyValue(AGGREGATE_ID, aggregateId),
                keyValue(AGGREGATE_TYPE, aggregateType),
                keyValue("payloadClass", payload.getClass().getSimpleName()),
                keyValue(ERROR_MESSAGE, e.getMessage())
            );

            throw new EventSerializationException(
                "Event serialization failed. See error log for details.", e
            );
        }
    }

    /**
     * Собирает текущий контекст трассировки, фильтруя пустые значения.
     * <p>
     * Использует {@link JsonInclude.Include#NON_NULL} для предотвращения
     * появления null-полей в итоговом JSON.
     *
     * @return JSON-строка или null, если контекст пуст.
     */
    private String buildTracingHeaders() {
        Map<String, String> tracingHeaders = new HashMap<>();

        Optional.ofNullable(tracer.currentSpan())
            .map(span -> span.context().traceId())
            .ifPresent(traceId -> tracingHeaders.put(TRACEPARENT, traceId));

        String[] baggageKeys = {
            CORRELATION_ID,
            CLIENT_ID,
            USER_AGENT,
            IP_ADDRESS
        };

        for (String key : baggageKeys) {
            Optional.ofNullable(tracer.getBaggage(key))
                .map(Baggage::get)
                .filter(value -> !value.isBlank())
                .ifPresent(value -> tracingHeaders.put(key, value));
        }

        if (tracingHeaders.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(tracingHeaders);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize tracing headers",
                keyValue(ERROR_MESSAGE, e.getMessage())
            );
            return null;
        }
    }
}
