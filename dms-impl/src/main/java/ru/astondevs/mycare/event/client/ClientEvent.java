package ru.astondevs.mycare.event.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
import ru.astondevs.mycare.event.BaseClientEvent;
import ru.astondevs.mycare.models.enums.ClientType;

/**
 * DTO-контракт для события.
 * <p>
 * Фиксирует факт создания записи о новом клиенте. Оно несет в себе основные данные,
 * необходимые для использования в данном мекросервисе.
 * </p>
 *
 * @param eventId    Уникальный идентификатор самого события.
 * @param clientId   Уникальный идентификатор созданного клиента.
 * @param firstName  Имя клиента.
 * @param lastName   Фамилия клиента.
 * @param middleName Отчество клиента (может быть {@code null}).
 * @param clientType Тип клиента.
 *
 * @author Ivan Sakharov
 * @since 11/16/2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientEvent(
    UUID eventId,
    UUID clientId,
    String firstName,
    String lastName,
    String middleName,
    ClientType clientType
) implements BaseClientEvent {
}
