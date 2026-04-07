package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о стоимости полиса.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о стоимости")
public class CostInfo {

    @Schema(description = "Сумма стоимости", example = "45000.5")
    private Double amount;

    @Schema(description = "Валюта", example = "RUB")
    private String currency;

    @Schema(description = "Форматированное отображение суммы", example = "45 000,50 ₽")
    private String formatted;
}
