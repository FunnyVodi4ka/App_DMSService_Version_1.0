package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о медицинском учреждении.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о медицинском учреждении")
public class MedicalInstitution {

    @Schema(description = "Тип учреждения", example = "Поликлиника")
    private String type;

    @Schema(description = "Наименование учреждения", example = "Городская поликлиника №1")
    private String name;

    @Schema(description = "Адрес учреждения", example = "г. Москва, ул. Ленина, д. 15")
    private String address;

    @Schema(description = "Номер телефона", example = "+7 (495) 123-45-67")
    private String phone;

    @Schema(description = "Email адрес", example = "info@polyclinic1.ru")
    private String email;
}
