package ru.astondevs.mycare.event.insuranceapplication;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;

/**
 * DTO-контракт для события "Заявление на страхование создано".
 * <p>
 * Фиксирует факт создания нового заявления.
 * Реализует общий контракт доменного события {@link DomainEvent}.
 *
 * @param eventId                Уникальный ID события.
 * @param insuranceApplicationId ID созданного заявления (ID агрегата).
 * @param dmsProgramId           ID связанной программы ДМС.
 * @param clientId               ID клиента.
 * @param clientType             Тип клиента.
 * @param insuranceApplicationNumber      Номер заявления.
 * @param initialStatus          Начальный статус (обычно PENDING).
 * @param occurredOn             Временная метка создания (из createdAt).
 */
@Builder
public record InsuranceApplicationCreatedEvent(
    UUID eventId,
    UUID insuranceApplicationId,
    UUID dmsProgramId,
    UUID clientId,
    ClientType clientType,
    String insuranceApplicationNumber,
    InsuranceApplicationStatus initialStatus,
    Instant occurredOn
) implements DomainEvent {
}
