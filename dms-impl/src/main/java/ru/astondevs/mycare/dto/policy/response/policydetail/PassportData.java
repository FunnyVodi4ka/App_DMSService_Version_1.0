package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные паспорта физического лица.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Данные паспорта")
public class PassportData {

    @Schema(description = "Серия паспорта", example = "4510")
    private String series;

    @Schema(description = "Номер паспорта", example = "123456")
    private String number;

    @Schema(description = "Код подразделения", example = "770-001")
    private String departmentCode;

    @Schema(description = "Наименование подразделения", example = "ОУФМС России по г. Москве")
    private String department;

    @Schema(description = "Дата выдачи паспорта", example = "2010-05-10")
    private String issueDate;
}
