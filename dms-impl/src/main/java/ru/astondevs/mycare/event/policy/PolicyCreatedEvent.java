package ru.astondevs.mycare.event.policy;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.models.enums.PolicyStatus;

/**
 * DTO-контракт для события "Полис создан".
 * <p>
 * Фиксирует факт создания нового полиса. Реализует общий контракт доменного события
 * {@link DomainEvent}.
 *
 * @param eventId       Уникальный ID события.
 * @param policyId      ID созданного полиса (ID агрегата).
 * @param clientId      ID клиента.
 * @param initialStatus Начальный статус (обычно Active).
 * @param number        Номер полиса.
 * @param occurredOn    Временная метка создания (из startDate).
 * @param policyUrl     Ссылка на документ полиса
 */
@Builder
public record PolicyCreatedEvent(
    UUID eventId,
    UUID policyId,
    UUID clientId,
    String number,
    PolicyStatus initialStatus,
    Instant occurredOn,
    String policyUrl
) implements DomainEvent {

}
