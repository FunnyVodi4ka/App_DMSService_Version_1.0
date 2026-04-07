package ru.astondevs.mycare.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import java.util.UUID;
import ru.astondevs.mycare.event.insuranceapplication.InsuranceApplicationCreatedEvent;
import ru.astondevs.mycare.event.insuranceapplication.InsuranceApplicationStatusChangedEvent;
import ru.astondevs.mycare.event.policy.PolicyCreatedEvent;
import ru.astondevs.mycare.event.policy.PolicyStatusChangedEvent;

/**
 * Интерфейс-контракт, описывающий доменное событие.
 * <p>
 * Все DTO событий должны реализовывать этот интерфейс.
 * <p>
 * Аннотации Jackson {@link JsonTypeInfo} и {@link JsonSubTypes} обеспечивают
 * полиморфную сериализацию/десериализацию для всех событий.
 * Это позволяет `OutboxService` обрабатывать `DomainEvent`
 * без знания о конкретной реализации.
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = InsuranceApplicationStatusChangedEvent.class,
        name = "InsuranceApplicationStatusChangedEvent"),
    @JsonSubTypes.Type(
        value = InsuranceApplicationCreatedEvent.class,
        name = "InsuranceApplicationCreatedEvent"),
    @JsonSubTypes.Type(
        value = PolicyCreatedEvent.class,
        name = "PolicyCreatedEvent"),
    @JsonSubTypes.Type(
        value = PolicyStatusChangedEvent.class,
        name = "PolicyStatusChangedEvent")
})
public interface DomainEvent {

    /**
     * Возвращает уникальный идентификатор события.
     * <p>
     * Используется для трассировки и обеспечения идемпотентности
     * (дедупликации) на стороне консьюмера.
     *
     * @return Уникальный идентификатор события (UUID).
     */
    UUID eventId();

    /**
     * Возвращает временную метку (в UTC), когда событие произошло.
     * <p>
     * Фиксирует точное время бизнес-факта и используется
     * для соблюдения порядка обработки.
     *
     * @return Временная метка ({@link Instant}).
     */
    Instant occurredOn();
}
