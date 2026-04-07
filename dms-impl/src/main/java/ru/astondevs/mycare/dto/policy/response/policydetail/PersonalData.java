package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Личные данные физического лица.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Личные данные")
public class PersonalData {

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество", example = "Петрович")
    private String middleName;

    @Schema(description = "Дата рождения", example = "1990-05-15")
    private String birthDate;

    @Schema(description = "Пол", example = "male", allowableValues = {"male", "female"})
    private String gender;

    @Schema(description = "Email адрес", example = "ivanov@email.com")
    private String email;

    @Schema(description = "Номер телефона", example = "+79991234567")
    private String phone;

    @Schema(description = "СНИЛС", example = "12345678900")
    private String snils;
}
