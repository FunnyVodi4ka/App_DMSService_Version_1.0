package ru.astondevs.mycare.dto.policy.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import ru.astondevs.mycare.models.enums.PolicyStatus;

/**
 * DTO для обновления статуса заявления на страхование.
 *
 * @param status   Новый статус, в который необходимо перевести полис ({@link PolicyStatus}).
 * @param clientId ID клиента, подающего заявление.
 * @author Ivan Segen
 * @version 1.0
 */
@Schema(description = "Данные для обновления статуса полиса")
public record UpdatePolicyStatusRequest(

    @Schema(
        description = "Новый статус полиса",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "inactive"
    )
    @NotNull(message = "Новый статус не может быть пустым")
    PolicyStatus status,

    @Schema(
        description = "ID клиента",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6"
    )
    @NotNull(message = "ID клиента не может быть пустым")
    UUID clientId
) {

}
