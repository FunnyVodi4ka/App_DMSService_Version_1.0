package ru.astondevs.mycare.event.insuranceapplication;

import com.fasterxml.jackson.annotation.JsonProperty; // <-- ВАЖНО
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;

/**
 * DTO-контракт для события "Статус заявления изменен".
 * <p>
 * Фиксирует факт изменения статуса существующего заявления.
 *
 * @param eventId                Уникальный ID этого события.
 * @param insuranceApplicationId ID заявления (агрегата), у которого изменился статус.
 * @param clientId               ID клиента, связанного с заявлением.
 * @param newStatus              Новый статус, который был установлен.
 * @param changedAt              Временная метка изменения (из updatedAt).
 * @param rejectionReport Опциональная причина (например, при отклонении).
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
@Builder
public record InsuranceApplicationStatusChangedEvent(

    @JsonProperty("eventId")
    UUID eventId,

    @JsonProperty("insuranceApplicationId")
    UUID insuranceApplicationId,

    @JsonProperty("clientId")
    UUID clientId,

    @JsonProperty("insuranceApplicationStatus")
    InsuranceApplicationStatus newStatus,

    @JsonProperty("changedAt")
    Instant changedAt,

    @JsonProperty("rejectionReport")
    String rejectionReport
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
