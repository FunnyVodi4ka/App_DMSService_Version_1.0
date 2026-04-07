package ru.astondevs.mycare.event;


import java.util.UUID;
import ru.astondevs.mycare.models.enums.ClientType;

/**
 * Базовый интерфейс для всех событий, связанных с клиентами.
 * <p>
 * Любая реализация события клиента (например, DTO или Record) должна предоставлять
 * доступ к этим ключевым метаданным для корректной маршрутизации и обработки.
 * </p>
 *
 * @author Ivan Sakharov
 * @since 11/17/2025
 */

public interface BaseClientEvent {

    /**
     * Возвращает уникальный идентификатор события.
     *
     * @return UUID идентификатор события.
     */
    UUID eventId();

    /**
     * Возвращает уникальный идентификатор клиента, с которым связано событие.
     * <p>
     * Является внешним ключом (reference) на сущность клиента в базе данных.
     * </p>
     *
     * @return UUID идентификатор клиента.
     */
    UUID clientId();

    /**
     * Возвращает тип клиента.
     *
     * @return Значение перечисления {@link ClientType}.
     */
    ClientType clientType();

}
