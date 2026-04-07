package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о страхователе (владельце полиса).
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о страхователе")
public class Policyholder {

    @Schema(description = "Личные данные страхователя")
    private PersonalData personalData;

    @Schema(description = "Данные паспорта страхователя")
    private PassportData passportData;

    @Schema(description = "Адрес регистрации")
    private Address registrationAddress;

    @Schema(description = "Адрес проживания")
    private Address residentialAddress;
}
