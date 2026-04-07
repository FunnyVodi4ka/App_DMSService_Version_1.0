package ru.astondevs.mycare.dto.policy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;
import ru.astondevs.mycare.logging.pii.api.annotation.PiiData;
import ru.astondevs.mycare.logging.pii.api.model.PiiStrategy;

/**
 * DTO для создания нового полиса.
 *
 * @param clientId  ID клиента, подающего заявление.
 * @param policyUrl Ссылка на документ полиса.
 * @param number    Номер полиса.
 * @author Ivan Segen
 * @version 1.0
 */
@Builder
@Schema(description = "Данные для создания нового полиса")
public record CreatePolicyRequest(

    @Schema(
        description = "ID клиента",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"
    )
    @NotNull(message = "ID клиента не может быть пустым")
    UUID clientId,

    @Schema(
        description = "ID контракта страхования",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"
    )
    @NotNull(message = "ID контракта страхования не может быть пустым")
    UUID insuranceContractId,

    @Schema(
        description = "Ссылка на документ полиса",
        maxLength = 255,
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    @NotBlank(message = "Ссылка на документ полиса не может быть пустой")
    @Size(max = 255, message = "Ссылка на документ полиса не может превышать 255 символов")
    String policyUrl,

    @Schema(
        description = "Номер полиса",
        maxLength = 50,
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "APP-2024-00123"
    )
    @NotBlank(message = "Номер полиса не может быть пустым")
    @Size(max = 50, message = "Номер полиса не может превышать 50 символов")
    @PiiData(strategy = PiiStrategy.ENCRYPT)
    String number
) {}
