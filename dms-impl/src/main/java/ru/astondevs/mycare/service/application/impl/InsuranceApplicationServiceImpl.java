package ru.astondevs.mycare.service.application.impl;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.ExceptionMessage.INSURANCE_APPLICATION_NOT_FOUND;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.mycare.dto.insuranceapplication.request.CreateInsuranceApplicationRequest;
import ru.astondevs.mycare.dto.insuranceapplication.request.UpdateApplicationStatusRequest;
import ru.astondevs.mycare.dto.insuranceapplication.response.InsuranceApplicationResponse;
import ru.astondevs.mycare.event.insuranceapplication.InsuranceApplicationCreatedEvent;
import ru.astondevs.mycare.event.insuranceapplication.InsuranceApplicationStatusChangedEvent;
import ru.astondevs.mycare.exception.insuranceapplication.InsuranceApplicationNotFoundException;
import ru.astondevs.mycare.kafkastarter.properties.KafkaStarterProperties;
import ru.astondevs.mycare.mapper.InsuranceApplicationMapper;
import ru.astondevs.mycare.models.entity.InsuranceApplication;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;
import ru.astondevs.mycare.repository.InsuranceApplicationRepository;
import ru.astondevs.mycare.service.application.InsuranceApplicationService;
import ru.astondevs.mycare.service.outbox.OutboxService;
import ru.astondevs.mycare.service.topic.TopicService;

/**
 * Реализация {@link InsuranceApplicationService},
 * управляющая основной бизнес-логикой заявлений.
 * <p>
 * Ответственна за координацию маппинга, сохранения в БД
 * и публикации доменных событий через {@link OutboxService}.
 * <p>
 * Логика поиска и валидации топиков Kafka
 * выполняется при инициализации бина в
 * {@link PostConstruct} методе.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceApplicationServiceImpl implements InsuranceApplicationService {

    /**
     * Сервис для поиска и валидации имен топиков Kafka.
     */
    private final TopicService topicService;

    /**
     * Конфигурационные свойства Kafka-стартера
     * (для доступа к списку топиков).
     */
    private final KafkaStarterProperties kafkaProperties;

    /**
     * Репозиторий для доступа к данным {@link InsuranceApplication}.
     */
    private final InsuranceApplicationRepository insuranceApplicationRepository;

    /**
     * MapStruct-маппер для преобразования DTO <-> Entity.
     * Отвечает только за трансформацию данных.
     */
    private final InsuranceApplicationMapper insuranceApplicationMapper;

    /**
     * Сервис для публикации событий в `outbox`.
     */
    private final OutboxService outboxService;

    /**
     * Единый топик Kafka для всех событий жизненного цикла заявления.
     * Инициализируется в {@link #initTopics()}.
     */
    private String topicInsuranceApplicationEvents;

    /**
     * Выполняет валидацию и инициализацию имен топиков
     * Kafka сразу после внедрения всех зависимостей.
     * <p>
     * Гарантирует, что сервис не запустится,
     * если требуемые топики не сконфигурированы
     * в {@link KafkaStarterProperties} (Fail-fast).
     */
    @PostConstruct
    protected void initTopics() {

        this.topicInsuranceApplicationEvents =
            topicService.findTopicNameOrFail(
                kafkaProperties,
                "insurance.dms-insurance-application-events"
            );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Устанавливает начальный статус (PENDING)
     * и публикует событие о создании.
     */
    @Override
    @Transactional
    public InsuranceApplicationResponse createInsuranceApplication(
        CreateInsuranceApplicationRequest request
    ) {
        InsuranceApplication insuranceApplication =
            insuranceApplicationMapper.toEntity(request);

        insuranceApplication.setInsuranceApplicationStatus(
            InsuranceApplicationStatus.PENDING
        );

        InsuranceApplication savedInsuranceApplication =
            insuranceApplicationRepository.save(insuranceApplication);

        InsuranceApplicationCreatedEvent eventPayload =
            buildCreatedEvent(savedInsuranceApplication);

        outboxService.createAndSaveEvent(
            topicInsuranceApplicationEvents,
            savedInsuranceApplication.getInsuranceApplicationId(),
            InsuranceApplication.class.getSimpleName(),
            eventPayload
        );

        return insuranceApplicationMapper.toResponse(savedInsuranceApplication);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Публикует событие {@link InsuranceApplicationStatusChangedEvent}
     * только в том случае, если новый статус
     * действительно отличается от старого.
     */
    @Override
    @Transactional
    public InsuranceApplicationResponse updateInsuranceApplicationStatus(
        UUID id,
        UpdateApplicationStatusRequest request
    ) {
        InsuranceApplication insuranceApplication = insuranceApplicationRepository
            .findById(id)
            .orElseThrow(() -> new InsuranceApplicationNotFoundException(
                INSURANCE_APPLICATION_NOT_FOUND.formatted(id))
            );

        InsuranceApplicationStatus oldStatus =
            insuranceApplication.getInsuranceApplicationStatus();

        InsuranceApplicationStatus newStatus =
            request.insuranceApplicationStatus();

        if (oldStatus == newStatus) {
            log.warn(
                "Skipping status update: status is the same",
                keyValue("applicationId", id),
                keyValue("status", oldStatus)
            );
            return insuranceApplicationMapper.toResponse(insuranceApplication);
        }

        insuranceApplication.setInsuranceApplicationStatus(newStatus);
        insuranceApplication.setComment(request.rejectionReport());

        InsuranceApplication updatedApplication =
            insuranceApplicationRepository.save(insuranceApplication);

        InsuranceApplicationStatusChangedEvent eventPayload =
            buildStatusChangedEvent(updatedApplication);

        outboxService.createAndSaveEvent(
            topicInsuranceApplicationEvents,
            updatedApplication.getInsuranceApplicationId(),
            InsuranceApplication.class.getSimpleName(),
            eventPayload
        );

        return insuranceApplicationMapper.toResponse(updatedApplication);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public InsuranceApplicationResponse getInsuranceApplicationById(UUID id) {

        return insuranceApplicationRepository
            .findById(id)
            .map(insuranceApplicationMapper::toResponse)
            .orElseThrow(() -> new InsuranceApplicationNotFoundException(
                INSURANCE_APPLICATION_NOT_FOUND.formatted(id))
            );
    }

    /**
     * Вспомогательный метод для сборки DTO события
     * {@link InsuranceApplicationCreatedEvent}
     * на основе сохраненной сущности.
     *
     * @param savedApp Сущность {@link InsuranceApplication}
     * сразу после сохранения.
     * @return DTO события
     * {@link InsuranceApplicationCreatedEvent}.
     */
    private InsuranceApplicationCreatedEvent buildCreatedEvent(
        InsuranceApplication savedApp
    ) {
        return InsuranceApplicationCreatedEvent
            .builder()
            .eventId(UUID.randomUUID())
            .insuranceApplicationId(savedApp.getInsuranceApplicationId())
            .dmsProgramId(savedApp.getDmsProgram().getDmsProgramId())
            .clientId(savedApp.getClientId())
            .clientType(savedApp.getClientType())
            .insuranceApplicationNumber(savedApp.getInsuranceApplicationNumber())
            .initialStatus(savedApp.getInsuranceApplicationStatus())
            .occurredOn(savedApp.getCreatedAt())
            .build();
    }

    /**
     * Вспомогательный метод для сборки DTO события
     * {@link InsuranceApplicationStatusChangedEvent}
     * на основе обновленной сущности.
     *
     * @param updatedApp Сущность {@link InsuranceApplication}
     * после обновления.
     * @return DTO события
     * {@link InsuranceApplicationStatusChangedEvent}.
     */
    private InsuranceApplicationStatusChangedEvent buildStatusChangedEvent(
        InsuranceApplication updatedApp
    ) {
        return InsuranceApplicationStatusChangedEvent
            .builder()
            .eventId(UUID.randomUUID())
            .insuranceApplicationId(updatedApp.getInsuranceApplicationId())
            .clientId(updatedApp.getClientId())
            .newStatus(updatedApp.getInsuranceApplicationStatus())
            .changedAt(updatedApp.getUpdatedAt())
            .rejectionReport(updatedApp.getComment())
            .build();
    }
}
