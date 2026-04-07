package ru.astondevs.mycare.dto.insuranceapplication.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;

/**
 * DTO, представляющее полную информацию о заявлении на страхование.
 *
 * @param insuranceApplicationId Уникальный ID заявления
 * @param dmsProgramId           ID связанной программы ДМС
 * @param clientId               ID клиента
 * @param employeeId             ID сотрудника, последнего обработавшего заявление
 * @param insuranceApplicationNumber                 Уникальный номер заявления
 * @param insuranceApplicationStatus                 Текущий статус заявления
 * @param clientType             Тип клиента
 * @param comment                Комментарий к заявлению
 * @param createdAt              Дата и время создания (UTC, ISO-8601)
 * @param updatedAt              Дата и время последнего обновления (UTC, ISO-8601)
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
@Builder
@Schema(description = "Полная информация о заявлении на страхование")
public record InsuranceApplicationResponse(

    @Schema(
        description = "Уникальный ID заявления",
        example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"
    )
    UUID insuranceApplicationId,

    @Schema(
        description = "ID связанной программы ДМС",
        example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    UUID dmsProgramId,

    @Schema(
        description = "ID клиента",
        example = "c9d0e1f2-a3b4-c5d6-e7f8-a9b0c1d2e3f4"
    )
    UUID clientId,

    @Schema(
        description = "ID сотрудника, последнего обработавшего заявление",
        example = "d0e1f2a3-b4c5-d6e7-f8a9-b0c1d2e3f4a5"
    )
    UUID employeeId,

    @Schema(
        description = "Уникальный номер заявления",
        example = "APP-2024-00123"
    )
    String insuranceApplicationNumber,

    @Schema(
        description = "Текущий статус заявления",
        example = "PENDING"
    )
    InsuranceApplicationStatus insuranceApplicationStatus,

    @Schema(
        description = "Тип клиента",
        example = "INDIVIDUAL"
    )
    ClientType clientType,

    @Schema(
        description = "Комментарий к заявлению",
        example = "Клиент просил связаться после 18:00"
    )
    String comment,

    @Schema(
        description = "Дата и время создания (UTC, ISO-8601)",
        example = "2024-10-21T10:15:30.00Z"
    )
    Instant createdAt,

    @Schema(
        description = "Дата и время последнего обновления (UTC, ISO-8601)",
        example = "2024-10-21T10:20:00.00Z"
    )
    Instant updatedAt
) {
}
