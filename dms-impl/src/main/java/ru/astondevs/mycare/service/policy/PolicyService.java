package ru.astondevs.mycare.service.policy;

import java.util.List;
import java.util.UUID;
import ru.astondevs.mycare.dto.policy.request.CreatePolicyRequest;
import ru.astondevs.mycare.dto.policy.request.UpdatePolicyStatusRequest;
import ru.astondevs.mycare.dto.policy.response.DmsPolicyDetailResponse;
import ru.astondevs.mycare.dto.policy.response.PolicyResponse;
import ru.astondevs.mycare.exception.policy.PolicyNotFoundException;
import ru.astondevs.mycare.models.entity.Policy;
import ru.astondevs.mycare.models.enums.ClientType;

/**
 * Определяет бизнес-контракт для управления {@link Policy}.
 * <p>
 * Абстрагирует всю бизнес-логику (создание, обновление, чтение) от слоя контроллеров. Все
 * реализации этого сервиса должны быть транзакционными.
 *
 * @author Mikhail Ermakov
 * @version 1.0
 */
public interface PolicyService {

    /**
     * Создает новый полис на основе DTO.
     * <p>
     * Реализация должна гарантировать атомарное сохранение и инициирование события о создании.
     *
     * @param request DTO с данными для создания.
     * @return DTO {@link PolicyResponse} с данными созданного полиса.
     */
    PolicyResponse createPolicy(
        CreatePolicyRequest request
    );

    /**
     * Обновляет статус существующего полиса.
     * <p>
     * Реализация должна гарантировать атомарное обновление и инициирование события об изменении
     * статуса.
     *
     * @param id      Уникальный идентификатор (UUID) полиса для обновления.
     * @param request DTO с новым статусом.
     * @return DTO {@link PolicyResponse} с обновленными данными полиса.
     * @throws PolicyNotFoundException если полис с {@code id} не найден.
     */
    PolicyResponse updatePolicyStatus(
        UUID id,
        UpdatePolicyStatusRequest request
    );

    /**
     * Выполняет поиск и возвращает DTO полиса по его ID.
     *
     * @param id Уникальный идентификатор (UUID) полиса.
     * @return DTO {@link PolicyResponse} с данными найденного полиса.
     * @throws PolicyNotFoundException если полис с {@code id} не найден.
     */
    PolicyResponse getPolicyById(UUID id);

    /**
     * Получить список полисов, отфильтрованных по типу клиента в заявке.
     *
     * @param clientType Тип клиента (например, REPRESENTATIVE)
     * @return Список DTO полисов
     */
    List<PolicyResponse> getPoliciesByClientType(ClientType clientType);

    /**
     * Возвращает подробную информацию о полисе ДМС физ. лица "Здоровье".
     * <p>
     * Используется специалистом страховой компании для просмотра полной информации о полисе,
     * включая данные страхователя, застрахованных лиц и доступные медицинские учреждения.
     *
     * @param policyId Уникальный идентификатор (UUID) полиса ДМС.
     * @return DTO {@link DmsPolicyDetailResponse} с полной информацией о полисе.
     * @throws PolicyNotFoundException если полис с {@code policyId} не найден.
     * @since 1.0.0
     */
    DmsPolicyDetailResponse getDmsPolicyDetail(UUID policyId);
}
