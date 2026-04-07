package ru.astondevs.mycare.service.outbox;

import java.util.UUID;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.models.entity.OutboxEvent;

/**
 * Интерфейс для универсального сервиса по работе с таблицей Outbox.
 * <p>
 * Абстрагирует логику создания и сохранения {@link OutboxEvent}
 * (включая сериализацию payload) в рамках основной бизнес-транзакции.
 * <p>
 * Это ключевой компонент паттерна "Transactional Outbox", гарантирующий
 * атомарную фиксацию бизнес-изменений и событий для Debezium.
 *
 * @author Ivan Sergienko
 * @version 1.1
 */
public interface OutboxService {

    /**
     * Создает и сохраняет событие в таблице Outbox.
     *
     * @param topic         Название топика в Kafka.
     * @param aggregateId   ID агрегата (сущности), к которому относится событие.
     * Используется как ключ партиционирования Kafka.
     * @param aggregateType Тип агрегата (для трассировки и DTO-маппинга).
     * @param payload       Объект-событие, реализующий {@link DomainEvent}.
     * @param <T>           Тип объекта-события.
     */
    <T extends DomainEvent> void createAndSaveEvent(
        String topic,
        UUID aggregateId,
        String aggregateType,
        T payload
    );
}
