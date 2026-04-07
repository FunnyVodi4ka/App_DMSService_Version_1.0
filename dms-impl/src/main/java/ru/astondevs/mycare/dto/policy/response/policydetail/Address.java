package ru.astondevs.mycare.dto.policy.response.policydetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Адресные данные (регистрация или проживание).
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Адресные данные")
public class Address {

    @Schema(description = "Страна", example = "Россия")
    private String country;

    @Schema(description = "Регион/область", example = "Московская область")
    private String region;

    @Schema(description = "Район", example = "Ленинский район")
    private String district;

    @Schema(description = "Город", example = "Москва")
    private String city;

    @Schema(description = "Улица", example = "Ленина")
    private String street;

    @Schema(description = "Номер дома", example = "15")
    private String houseNumber;

    @Schema(description = "Номер строения", example = "1")
    private String buildingNumber;

    @Schema(description = "Номер подъезда", example = "2")
    private String porchNumber;

    @Schema(description = "Номер квартиры", example = "45")
    private String flatNumber;

    @Schema(description = "Номер комнаты")
    private String roomNumber;

    @Schema(description = "Почтовый индекс", example = "101000")
    private String postalCode;
}
