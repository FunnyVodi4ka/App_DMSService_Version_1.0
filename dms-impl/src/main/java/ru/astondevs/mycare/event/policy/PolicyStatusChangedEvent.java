package ru.astondevs.mycare.event.policy;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.models.enums.PolicyStatus;

/**
 * DTO-контракт для события "Статус полиса изменен".
 * <p>
 * Фиксирует факт изменения статуса существующего полиса.
 *
 * @param eventId         Уникальный ID этого события.
 * @param policyId        ID полиса (агрегата), у которого изменился статус.
 * @param clientId               ID клиента, связанного с заявлением.
 * @param newStatus       Новый статус, который был установлен.
 * @param changedAt       Временная метка изменения (из updatedAt).
 * @author Ivan Segen
 * @version 1.0
 */
@Builder
public record PolicyStatusChangedEvent(

    @JsonProperty("eventId")
    UUID eventId,

    @JsonProperty("policyId")
    UUID policyId,

    @JsonProperty("clientId")
    UUID clientId,

    @JsonProperty("policyStatus")
    PolicyStatus newStatus,

    @JsonProperty("changedAt")
    Instant changedAt

) implements DomainEvent {

    /**
     * Реализация метода интерфейса {@link DomainEvent}.
     * Используется для логики сериализации в @JsonTypeInfo.
     */
    @Override
    public UUID eventId() {
        return eventId;
    }

    /**
     * Реализация метода интерфейса {@link DomainEvent}.
     * Маппим 'changedAt' на 'occurredOn' для
     * совместимости с интерфейсом.
     */
    @Override
    public Instant occurredOn() {
        return changedAt;
    }
}
