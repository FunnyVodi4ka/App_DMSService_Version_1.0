package ru.astondevs.mycare.service.application;

import java.util.UUID;
import ru.astondevs.mycare.dto.insuranceapplication.request.CreateInsuranceApplicationRequest;
import ru.astondevs.mycare.dto.insuranceapplication.request.UpdateApplicationStatusRequest;
import ru.astondevs.mycare.dto.insuranceapplication.response.InsuranceApplicationResponse;
import ru.astondevs.mycare.exception.insuranceapplication.InsuranceApplicationNotFoundException;
import ru.astondevs.mycare.models.entity.InsuranceApplication;

/**
 * Определяет бизнес-контракт для управления {@link InsuranceApplication}.
 * <p>
 * Абстрагирует всю бизнес-логику (создание, обновление, чтение)
 * от слоя контроллеров. Все реализации этого сервиса
 * должны быть транзакционными.
 *
 * @author Ivan Sergienko
 * @version 1.1
 */
public interface InsuranceApplicationService {

    /**
     * Создает новое заявление на страхование на основе DTO.
     * <p>
     * Реализация должна гарантировать атомарное сохранение
     * и инициирование события о создании.
     *
     * @param request DTO с данными для создания.
     * @return DTO {@link InsuranceApplicationResponse} с данными созданного заявления.
     */
    InsuranceApplicationResponse createInsuranceApplication(
        CreateInsuranceApplicationRequest request
    );

    /**
     * Обновляет статус (и опционально комментарий) существующего заявления.
     * <p>
     * Реализация должна гарантировать атомарное обновление
     * и инициирование события об изменении статуса.
     *
     * @param id Уникальный идентификатор (UUID) заявления для обновления.
     * @param request DTO с новым статусом и (опционально) причиной/комментарием.
     * @return DTO {@link InsuranceApplicationResponse} с обновленными данными заявления.
     * @throws InsuranceApplicationNotFoundException если заявление с {@code id} не найдено.
     */
    InsuranceApplicationResponse updateInsuranceApplicationStatus(
        UUID id,
        UpdateApplicationStatusRequest request
    );

    /**
     * Выполняет поиск и возвращает DTO заявления по его ID.
     *
     * @param id Уникальный идентификатор (UUID) заявления.
     * @return DTO {@link InsuranceApplicationResponse} с данными найденного заявления.
     * @throws InsuranceApplicationNotFoundException если заявление с {@code id} не найдено.
     */
    InsuranceApplicationResponse getInsuranceApplicationById(UUID id);
}
