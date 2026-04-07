package ru.astondevs.mycare.service.policy.impl;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static ru.astondevs.mycare.util.ExceptionMessage.POLICY_NOT_FOUND;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.mycare.dto.policy.request.CreatePolicyRequest;
import ru.astondevs.mycare.dto.policy.request.UpdatePolicyStatusRequest;
import ru.astondevs.mycare.dto.policy.response.DmsPolicyDetailResponse;
import ru.astondevs.mycare.dto.policy.response.PolicyResponse;
import ru.astondevs.mycare.event.policy.PolicyCreatedEvent;
import ru.astondevs.mycare.event.policy.PolicyStatusChangedEvent;
import ru.astondevs.mycare.exception.policy.PolicyNotFoundException;
import ru.astondevs.mycare.kafkastarter.properties.KafkaStarterProperties;
import ru.astondevs.mycare.mapper.PolicyMapper;
import ru.astondevs.mycare.models.entity.InsuredPerson;
import ru.astondevs.mycare.models.entity.Policy;
import ru.astondevs.mycare.models.entity.PolicyMedicalFacility;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.models.enums.PolicyStatus;
import ru.astondevs.mycare.repository.InsuredPersonRepository;
import ru.astondevs.mycare.repository.PolicyMedicalFacilityRepository;
import ru.astondevs.mycare.repository.PolicyRepository;
import ru.astondevs.mycare.service.outbox.OutboxService;
import ru.astondevs.mycare.service.policy.PolicyService;
import ru.astondevs.mycare.service.topic.TopicService;

/**
 * Реализация {@link PolicyService}, управляющая основной бизнес-логикой полисов.
 * <p>
 * Ответственна за координацию маппинга, сохранения в БД и публикации доменных событий через
 * {@link OutboxService}.
 * <p>
 * Логика поиска и валидации топиков Kafka выполняется при инициализации бина в
 * {@link PostConstruct} методе.
 *
 * @author Ivan Segen
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    /**
     * Репозиторий для доступа к данным {@link Policy}.
     */
    private final PolicyRepository policyRepository;

    /**
     * Репозиторий для доступа к данным {@link InsuredPerson}.
     */
    private final InsuredPersonRepository insuredPersonRepository;

    /**
     * Репозиторий для доступа к данным {@link PolicyMedicalFacility}.
     */
    private final PolicyMedicalFacilityRepository policyMedicalFacilityRepository;

    /**
     * MapStruct-маппер для преобразования DTO <-> Entity. Отвечает только за трансформацию данных.
     */
    private final PolicyMapper policyMapper;

    /**
     * Сервис для поиска и валидации имен топиков Kafka.
     */
    private final TopicService topicService;

    /**
     * Конфигурационные свойства Kafka-стартера (для доступа к списку топиков).
     */
    private final KafkaStarterProperties kafkaProperties;

    /**
     * Сервис для публикации событий в `outbox`.
     */
    private final OutboxService outboxService;

    /**
     * Единый топик Kafka для всех событий жизненного цикла полиса. Инициализируется в
     * {@link #initTopics()}.
     */
    private String topicPolicyEvents;

    /**
     * Выполняет валидацию и инициализацию имен топиков Kafka сразу после внедрения всех
     * зависимостей.
     * <p>
     * Гарантирует, что сервис не запустится, если требуемые топики не сконфигурированы в
     * {@link KafkaStarterProperties} (Fail-fast).
     */
    @PostConstruct
    protected void initTopics() {

        this.topicPolicyEvents = topicService.findTopicNameOrFail(
            kafkaProperties,
            "insurance.dms-policy-events"
        );
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> getPoliciesByClientType(ClientType clientType) {

        List<Policy> policies = policyRepository.findAllByClientType(clientType);

        return policyMapper.toDtoList(policies);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Устанавливает начальный статус (ACTIVE) и публикует событие о создании.
     */
    @Override
    @Transactional
    public PolicyResponse createPolicy(CreatePolicyRequest request) {
        Policy policy = policyMapper.toEntity(request);

        policy.setStatus(PolicyStatus.ACTIVE);

        Policy savedPolicy = policyRepository.save(policy);

        PolicyCreatedEvent eventPayload = buildCreatedEvent(savedPolicy, request.clientId());

        outboxService.createAndSaveEvent(
            topicPolicyEvents,
            savedPolicy.getPolicyId(),
            Policy.class.getSimpleName(),
            eventPayload
        );

        return policyMapper.toDto(savedPolicy);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Публикует событие {@link PolicyStatusChangedEvent} только в том случае, если новый статус
     * действительно отличается от старого.
     */
    @Override
    @Transactional
    public PolicyResponse updatePolicyStatus(
        UUID id,
        UpdatePolicyStatusRequest request
    ) {
        Policy policy = policyRepository
            .findById(id)
            .orElseThrow(() -> new PolicyNotFoundException(
                POLICY_NOT_FOUND.formatted(id))
            );

        PolicyStatus oldStatus = policy.getStatus();

        PolicyStatus newStatus = request.status();

        if (oldStatus == newStatus) {
            log.warn(
                "Skipping status update: status is the same",
                keyValue("policyId", id),
                keyValue("status", oldStatus)
            );
            return policyMapper.toDto(policy);
        }

        policy.setStatus(newStatus);

        Policy updatedPolicy = policyRepository.save(policy);

        PolicyStatusChangedEvent eventPayload =
            buildStatusChangedEvent(updatedPolicy, request.clientId());

        outboxService.createAndSaveEvent(
            topicPolicyEvents,
            updatedPolicy.getPolicyId(),
            Policy.class.getSimpleName(),
            eventPayload
        );

        return policyMapper.toDto(updatedPolicy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(UUID id) {

        return policyRepository
            .findById(id)
            .map(policyMapper::toDto)
            .orElseThrow(() -> new PolicyNotFoundException(
                POLICY_NOT_FOUND.formatted(id))
            );
    }

    /**
     * Вспомогательный метод для сборки DTO события {@link PolicyCreatedEvent} на основе сохраненной
     * сущности.
     *
     * @param savedPolicy Сущность {@link Policy} сразу после сохранения.
     * @return DTO события {@link PolicyCreatedEvent}.
     */
    private PolicyCreatedEvent buildCreatedEvent(
        Policy savedPolicy,
        UUID clientId
    ) {
        return PolicyCreatedEvent
            .builder()
            .eventId(UUID.randomUUID())
            .policyId(savedPolicy.getPolicyId())
            .clientId(clientId)
            .number(savedPolicy.getNumber())
            .initialStatus(savedPolicy.getStatus())
            .occurredOn(savedPolicy.getStartDate())
            .policyUrl(savedPolicy.getPolicyUrl())
            .build();
    }

    /**
     * Вспомогательный метод для сборки DTO события {@link PolicyStatusChangedEvent} на основе
     * обновленной сущности.
     *
     * @param updatedPolicy Сущность {@link Policy} после обновления.
     * @return DTO события {@link PolicyStatusChangedEvent}.
     */
    private PolicyStatusChangedEvent buildStatusChangedEvent(
        Policy updatedPolicy,
        UUID clientId
    ) {
        return PolicyStatusChangedEvent
            .builder()
            .eventId(UUID.randomUUID())
            .policyId(updatedPolicy.getPolicyId())
            .clientId(clientId)
            .newStatus(updatedPolicy.getStatus())
            .changedAt(updatedPolicy.getUpdatedAt())
            .build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Возвращает подробную информацию о полисе ДМС, включая данные страхователя, застрахованных лиц
     * и доступные медицинские учреждения.
     *
     * @since 1.0.0
     */
    @Override
    @Transactional(readOnly = true)
    public DmsPolicyDetailResponse getDmsPolicyDetail(UUID policyId) {

        log.info("Получение подробных сведений о полисе DMS",
            keyValue("policyId", policyId)
        );

        Policy policy = policyRepository
            .findByIdWithDetails(policyId)
            .orElseThrow(() -> {
                log.error("Полис DMS не найден",
                    keyValue("policyId", policyId)
                );
                return new PolicyNotFoundException(
                    POLICY_NOT_FOUND.formatted(policyId)
                );
            });

        log.info("Успешно извлеченные сведения о полисе DMS",
            keyValue("policyId", policyId)
        );

        DmsPolicyDetailResponse response = policyMapper.toDmsPolicyDetailResponse(policy);

        UUID insuranceApplicationId = policy.getInsuranceContract()
            .getInsuranceApplication()
            .getInsuranceApplicationId();

        List<InsuredPerson> insuredPersons = insuredPersonRepository
            .findByInsuranceApplicationInsuranceApplicationId(insuranceApplicationId);

        response.setInsuredPersons(
            insuredPersons.stream()
                .map(policyMapper::toInsuredPersonTable)
                .toList()
        );

        List<PolicyMedicalFacility> medicalFacilities = policyMedicalFacilityRepository
            .findByPolicyPolicyId(policyId);

        response.setMedicalInstitutions(
            medicalFacilities.stream()
                .map(pmf -> policyMapper.toMedicalInstitution(pmf.getMedicalFacility()))
                .toList()
        );

        return response;
    }

}
