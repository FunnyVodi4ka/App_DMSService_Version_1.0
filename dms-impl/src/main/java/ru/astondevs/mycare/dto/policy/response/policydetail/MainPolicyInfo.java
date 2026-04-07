package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Основная информация о полисе ДМС.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Основная информация о полисе")
public class MainPolicyInfo {

    @Schema(description = "Номер полиса", example = "BB-DMS-123456789")
    private String policyNumber;

    @Schema(description = "Статус полиса", example = "active", allowableValues = {"active",
        "inactive", "expired", "cancelled"})
    private String status;

    @Schema(description = "Тип программы ДМС", example = "maximum", allowableValues = {"maximum",
        "standard", "basic"})
    private String programType;

    @Schema(description = "Доступные услуги по полису")
    private List<String> availableServices;

    @Schema(description = "Дата начала действия полиса", example = "2024-01-15")
    private String startDate;

    @Schema(description = "Дата окончания действия полиса", example = "2024-12-31")
    private String endDate;

    @Schema(description = "Стоимость полиса")
    private CostInfo cost;

    @Schema(description = "Дата и время создания полиса", example = "2024-01-10T09:30:00Z")
    private String createdAt;
}
