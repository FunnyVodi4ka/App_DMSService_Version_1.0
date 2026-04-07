package ru.astondevs.mycare.consumer.client;


import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.constants.OutboxDomainConstants.EVENT_TYPE;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.mycare.event.client.ClientEvent;
import ru.astondevs.mycare.models.entity.Client;
import ru.astondevs.mycare.service.client.ClientService;
import ru.astondevs.mycare.util.constants.KafkaEventType;

/**
 * Kafka consumer, обрабатывающий event на создание и изменение данных о клиентах. {@link Client} в
 * базу данных.
 *
 * @author Ivan Sakharov
 * @since 11/16/2025
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final ClientService clientService;

    private final Map<KafkaEventType, Consumer<ClientEvent>> actions =
        new EnumMap<>(KafkaEventType.class);

    /**
     * Список полей, изменения в которых требуют обновления записи в БД. Если в событии UPDATE
     * изменены поля, не входящие в этот список, запись в БД не будет обновлена.
     */
    private static final Set<String> TRACKED_ENTITY_FIELDS = Set.of(
        Client.Fields.firstName,
        Client.Fields.lastName,
        Client.Fields.middleName,
        Client.Fields.clientType
    );

    @PostConstruct
    public void init() {
        actions.put(KafkaEventType.CREATE, clientService::createClient);
        actions.put(KafkaEventType.UPDATE, clientService::updateClient);
    }

    /**
     * Метод обработки списка событий kafka связанных с манипуляциями над сущностью Client.
     *
     * @param records список событий.
     */
    @KafkaListener(
        topics = "${app.kafka.topics.client-events}",
        groupId = "${starter.kafka.consumer.group-id}",
        properties = {
            "spring.json.value.default.type=ru.astondevs.mycare.event.client.ClientEvent",
            "spring.json.trusted.packages=*"
        }
    )
    public void handleClientEvents(List<ConsumerRecord<String, ClientEvent>> records) {

        for (ConsumerRecord<String, ClientEvent> record : records) {
            if (record == null) {
                log.warn("Received null record");
                continue;
            }
            ClientEvent event = record.value();
            if (event == null) {
                log.warn("Payload is empty for record",
                    keyValue("offset", record.offset()));
                continue;
            }

            String eventTypeHeader = extractEventTypeHeaderValue(record);
            var eventType = KafkaEventType.fromDescription(eventTypeHeader).orElse(null);

            if (eventType == null) {
                log.warn("Missing event type");
                continue;
            }

            if (KafkaEventType.UPDATE == eventType && !hasTrackedFields(record.headers())) {
                log.info("Skipping UPDATE: no tracked fields changed",
                    keyValue("clientId", record.value().clientId()));
                continue;
            }

            var action = actions.get(eventType);
            if (action != null) {
                Runnable runnable = () -> action.accept(event);
                handleSingleEvent(runnable, eventTypeHeader, event.clientId());
            } else {
                log.warn("No action defined for event type: {}. Event ignored.",
                    eventType,
                    keyValue("clientId", record.value().clientId()));
            }
        }
    }

    /**
     * Проверяет наличие отслеживаемых полей в заголовках Kafka-сообщения.
     * <p>
     * Метод используется для определения необходимости обработки события (чаще всего типа UPDATE).
     * Если в заголовках отсутствует хотя бы один ключ, входящий в {@link #TRACKED_ENTITY_FIELDS},
     * значит, изменения в исходной системе не затрагивают поля, потребляемые данным микросервисом.
     * </p>
     *
     * @param headers заголовки полученной записи {@link org.apache.kafka.common.header.Headers}.
     * @return {@code true}, если в заголовках найдено хотя бы одно поле из списка отслеживаемых;
     * {@code false} в противном случае.
     */

    private boolean hasTrackedFields(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
            .anyMatch(header -> TRACKED_ENTITY_FIELDS.contains(header.key()));
    }


    /**
     * Универсальный метод для безопасной обработки отдельного события из пачки. Выполняет
     * переданное действие в блоке try-catch, предотвращая остановку обработки всего списка событий
     * при ошибке в одном из них.
     *
     * @param action    функциональный интерфейс с логикой обработки (вызов сервиса).
     * @param eventType название операции для идентификации контекста ошибки в логах.
     */
    private void handleSingleEvent(Runnable action, String eventType, UUID clientId) {
        try {
            action.run();
        } catch (RuntimeException e) {
            log.error("Failed to save client to database",
                keyValue("eventType", eventType),
                keyValue("clientId", clientId),
                keyValue("error", e.getMessage()),
                e
            );
            throw e;
        }
    }

    /**
     * Извлекает строковое значение заголовка типа события из записи Kafka.
     *
     * <p>Метод обращается к заголовкам записи
     * ищет последний заголовок с ключом {@code EVENT_TYPE}. Если заголовок найден и содержит
     * данные, его байтовое значение декодируется в строку с использованием кодировки UTF-8.
     *
     * @param record Запись {@link ConsumerRecord}, содержащая заголовки и данные события.
     * @return Значение заголовка в виде строки или {@code null}, если заголовок с таким ключом
     * отсутствует или его значение пустое.
     */
    private String extractEventTypeHeaderValue(ConsumerRecord<?, ?> record) {
        Header eventTypeHeader = record.headers().lastHeader(EVENT_TYPE);

        if (eventTypeHeader != null && eventTypeHeader.value() != null) {
            return new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
        }

        return null;
    }
}
