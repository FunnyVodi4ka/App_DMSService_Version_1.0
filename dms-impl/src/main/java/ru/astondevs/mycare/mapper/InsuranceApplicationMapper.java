package ru.astondevs.mycare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.astondevs.mycare.dto.insuranceapplication.request.CreateInsuranceApplicationRequest;
import ru.astondevs.mycare.dto.insuranceapplication.response.InsuranceApplicationResponse;
import ru.astondevs.mycare.models.entity.InsuranceApplication;

/**
 * Маппер для преобразования между DTO и Entity {@link InsuranceApplication}.
 * <p>
 * Использует MapStruct для генерации реализации, обеспечивая
 * безопасность типов и высокую производительность.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InsuranceApplicationMapper {

    /**
     * Преобразует DTO для создания в сущность {@link InsuranceApplication}.
     * <p>
     * Игнорирует поля, которые должны быть установлены
     * бизнес-логикой (статус) или JPA (ID, createdAt, updatedAt, version).
     * <b>Важно:</b> Поле {@code insuranceApplicationId} помечено как {@code ignore = true}.
     * Это необходимо, чтобы MapStruct использовал значение по умолчанию, генерируемое
     * в сущности (UUID v7), а не пытался передать null в билдер.
     *
     * @param request DTO с данными для создания.
     * @return Новая, частично заполненная
     * сущность {@link InsuranceApplication}.
     */
    @Mapping(target = "dmsProgram.dmsProgramId", source = "dmsProgramId")
    @Mapping(target = "insuranceApplicationId", ignore = true)
    @Mapping(target = "insuranceApplicationStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isNew", ignore = true)
    InsuranceApplication toEntity(CreateInsuranceApplicationRequest request);

    /**
     * Преобразует сущность {@link InsuranceApplication} в DTO для ответа.
     * <p>
     * "Уплощает" вложенную программу ДМС до ее ID
     * ({@code entity.dmsProgram.dmsProgramId -> response.dmsProgramId}).
     *
     * @param entity Сущность из базы данных.
     * @return DTO {@link InsuranceApplicationResponse}.
     */
    @Mapping(target = "dmsProgramId", source = "dmsProgram.dmsProgramId")
    InsuranceApplicationResponse toResponse(InsuranceApplication entity);
}
