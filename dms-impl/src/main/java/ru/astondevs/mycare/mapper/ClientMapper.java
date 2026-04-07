package ru.astondevs.mycare.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.astondevs.mycare.event.client.ClientEvent;
import ru.astondevs.mycare.models.entity.Client;

/**
 * Маппер для преобразования между DTO и Entity {@link Client}.
 * <p>
 * Использует MapStruct для генерации реализации, обеспечивая безопасность типов и высокую
 * производительность.
 *
 * @author Ivan Sakharov
 * @since 11/17/2025
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    /**
     * Преобразует событие создания клиента в сущность клиента.
     * <p>
     * Метод используется при получении {@link ClientEvent} для формирования нового объекта
     * {@link Client}, готового к сохранению в базу данных. Явно маппится поле {@code clientType}.
     * </p>
     *
     * @param clientEvent Событие (DTO), содержащее данные для создания нового клиента.
     * @return Сущность {@link Client}, заполненная данными из события.
     */
    @Mapping(target = "clientType", source = "clientEvent.clientType")
    Client changeEntity(ClientEvent clientEvent);

    /**
     * Преобразует событие обновления данных клиента в сущность клиента.
     * <p>
     * Метод используется при получении {@link ClientEvent} для формирования объекта
     * {@link Client} с обновленными данными. Явно маппится поле {@code clientType}.
     * </p>
     *
     * @param clientEvent Событие (DTO), содержащее данные для обновления клиента.
     * @return Сущность {@link Client}, заполненная обновленными данными из события.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "clientId", ignore = true)
    void changeEntity(@MappingTarget Client existingClient, ClientEvent clientEvent);
}
