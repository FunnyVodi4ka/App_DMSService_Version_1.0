package ru.astondevs.mycare.dto.insuranceapplication.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.models.enums.ClientType;

/**
 * DTO для создания нового заявления на страхование.
 *
 * @param dmsProgramId ID программы ДМС, к которой относится заявление.
 * @param clientId     ID клиента, подающего заявление.
 * @param employeeId   ID сотрудника, регистрирующего заявление.
 * @param clientType   Тип клиента ({@link ClientType}).
 * @param insuranceApplicationNumber       Уникальный номер заявления (бизнес-ключ).
 * @param comment      Комментарий (опционально).
 * @author Ivan Sergienko
 * @version 1.1
 */
@Builder
@Schema(description = "Данные для создания нового заявления на страхование")
public record CreateInsuranceApplicationRequest(

    @Schema(
        description = "ID программы ДМС",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    @NotNull(message = "ID программы ДМС не может быть пустым")
    UUID dmsProgramId,

    @Schema(
        description = "ID клиента",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"
    )
    @NotNull(message = "ID клиента не может быть пустым")
    UUID clientId,

    @Schema(
        description = "ID сотрудника",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "b2c3d4e5-f6a7-b8c9-d0e1-f2a3b4c5d6e7"
    )
    @NotNull(message = "ID сотрудника не может быть пустым")
    UUID employeeId,

    @Schema(
        description = "Тип клиента",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "INDIVIDUAL"
    )
    @NotNull(message = "Тип клиента не может быть пустым")
    ClientType clientType,

    @Schema(
        description = "Номер заявления (бизнес-ключ)",
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "APP-2024-00123"
    )
    @NotBlank(message = "Номер заявления не может быть пустым")
    @Size(max = 50, message = "Номер заявления не может превышать 50 символов")
    String insuranceApplicationNumber,

    @Schema(
        description = "Комментарий (опционально)",
        maxLength = 400,
        example = "Клиент просил связаться после 18:00"
    )
    @Size(max = 400, message = "Комментарий не может превышать 400 символов")
    String comment
) {
}
