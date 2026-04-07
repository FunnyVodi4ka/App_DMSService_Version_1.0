package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о застрахованном лице.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о застрахованном лице")
public class InsuredPersonTable {

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Отчество", example = "Петрович")
    private String middleName;

    @Schema(description = "Дата рождения", example = "1990-05-15")
    private String birthDate;

    @Schema(description = "Пол", example = "male", allowableValues = {"male", "female"})
    private String gender;

    @Schema(description = "Тип застрахованного лица", example = "policyholder", allowableValues = {
        "policyholder", "family_member"})
    private String type;
}
