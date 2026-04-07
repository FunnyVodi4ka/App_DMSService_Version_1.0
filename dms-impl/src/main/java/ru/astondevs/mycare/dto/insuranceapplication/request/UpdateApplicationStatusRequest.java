package ru.astondevs.mycare.dto.insuranceapplication.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;

/**
 * DTO для обновления статуса заявления на страхование.
 *
 * @param insuranceApplicationStatus Новый статус, в который
 * необходимо перевести заявление ({@link InsuranceApplicationStatus}).
 * @param rejectionReport Опциональный комментарий, (например,
 * причина отклонения).
 * @author Ivan Sergienko
 * @version 1.0
 */
@Schema(description = "Данные для обновления статуса заявления")
public record UpdateApplicationStatusRequest(

    @Schema(
        description = "Новый статус заявления",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "APPROVED"
    )
    @NotNull(message = "Новый статус не может быть пустым")
    InsuranceApplicationStatus insuranceApplicationStatus,

    @Schema(
        description = "Причина изменения (например, причина отклонения)",
        maxLength = 400,
        example = "Неполный комплект документов"
    )
    @Size(max = 400, message = "Комментарий не может превышать 400 символов")
    String rejectionReport
) {
}
