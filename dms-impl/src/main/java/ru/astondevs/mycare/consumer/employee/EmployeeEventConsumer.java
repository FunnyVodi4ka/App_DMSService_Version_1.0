package ru.astondevs.mycare.consumer.employee;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.astondevs.mycare.event.employee.EmployeeEvent;
import ru.astondevs.mycare.models.entity.Employee;
import ru.astondevs.mycare.service.employee.EmployeeService;
import ru.astondevs.mycare.util.kafka.ConsumerRecordHeaderUtils;
import ru.astondevs.mycare.util.kafka.KafkaEventConverter;

/**
 * Kafka consumer, обрабатывающий топик о создании и изменении данных о сотрудниках. Сохраняет
 * {@link ru.astondevs.mycare.models.entity.Employee} в базу данных.
 *
 * @author Mikhail Ermakov
 * @since 12/11/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeEventConsumer {

    private final EmployeeService employeeService;
    private final KafkaEventConverter kafkaEventConverter;

    /**
     * Список полей, изменения в которых требуют обновления записи в БД. Если в событии UPDATE
     * изменены поля, не входящие в этот список, событие будет проигнорировано.
     */
    private static final Set<String> TRACKED_FIELDS = Set.of(
            Employee.Fields.employeeRole,
            Employee.Fields.lastName,
            Employee.Fields.firstName,
            Employee.Fields.middleName,
            Employee.Fields.managerId
    );

    /**
     * Основной метод обработки пакета событий.
     *
     * <p>Принимает список записей {@link ConsumerRecord}.
     *
     * @param records список записей из Kafka (batch).
     */
    @KafkaListener(
            topics = "${app.kafka.topics.employee-events}",
            groupId = "${starter.kafka.consumer.group-id}")
    public void handleEmployeeEvents(List<ConsumerRecord<String, Object>> records) {

        log.info("Received employee events batch", keyValue("batchSize", records.size()));

        for (ConsumerRecord<String, Object> record : records) {

            Object value = record.value();
            if (value == null) {
                continue;
            }

            try {
                EmployeeEvent event = kafkaEventConverter.convert(value, EmployeeEvent.class);
                if (event != null) {
                    processRecord(record, event);
                }
            } catch (Exception e) {
                log.error("Failed to process employee event", keyValue("error", e.getMessage()), e);
            }
        }
    }

    private void processRecord(ConsumerRecord<String, Object> record, EmployeeEvent event) {
        String eventType = ConsumerRecordHeaderUtils.extractHeaderAsString(record, "eventType");

        if (eventType == null) {
            log.warn(
                    "Event type header is missing, processing as generic update",
                    keyValue("employeeId", event.employeeId()));
            employeeService.createOrUpdateEmployee(event);
            return;
        }

        switch (eventType) {
            case "CREATE" -> {
                log.info("Processing CREATE event", keyValue("employeeId", event.employeeId()));
                employeeService.createOrUpdateEmployee(event);
            }
            case "UPDATE" -> handleUpdateEvent(record, event);
            default -> log.info(
                    "Skipping unsupported event type",
                    keyValue("eventType", eventType),
                    keyValue("employeeId", event.employeeId()));
        }
    }

    private void handleUpdateEvent(ConsumerRecord<String, Object> record, EmployeeEvent event) {
        String changedFieldsJson =
                ConsumerRecordHeaderUtils.extractHeaderAsString(record, "changed_fields");

        List<String> changedFields = kafkaEventConverter.convertJsonToList(changedFieldsJson, String.class);

        boolean hasRelevantChanges =
                changedFields.isEmpty() || changedFields.stream().anyMatch(TRACKED_FIELDS::contains);

        if (hasRelevantChanges) {
            log.info(
                    "Processing UPDATE event with relevant fields",
                    keyValue("employeeId", event.employeeId()),
                    keyValue("changedFields", changedFields));
            employeeService.createOrUpdateEmployee(event);
        } else {
            log.info(
                    "Skipping UPDATE event: no relevant fields changed",
                    keyValue("employeeId", event.employeeId()),
                    keyValue("changedFields", changedFields));
        }
    }
}
