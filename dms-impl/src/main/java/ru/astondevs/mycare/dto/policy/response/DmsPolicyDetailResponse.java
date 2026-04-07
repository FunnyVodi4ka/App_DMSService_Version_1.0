package ru.astondevs.mycare.dto.policy.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.astondevs.mycare.dto.policy.response.policydetail.InsuredPersonTable;
import ru.astondevs.mycare.dto.policy.response.policydetail.MainPolicyInfo;
import ru.astondevs.mycare.dto.policy.response.policydetail.MedicalInstitution;
import ru.astondevs.mycare.dto.policy.response.policydetail.Policyholder;

/**
 * DTO для получения подробной информации о полисе ДМС физ. лица. Содержит полную информацию о
 * полисе, страхователе, застрахованных лицах и доступных медицинских учреждениях.
 *
 * @author Aleksandr Zuev
 * @version 1.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Подробная информация о полисе ДМС")
public class DmsPolicyDetailResponse {

    @Schema(description = "Основная информация о полисе")
    private MainPolicyInfo mainInfo;

    @Schema(description = "Информация о страхователе")
    private Policyholder policyholder;

    @Schema(description = "Список застрахованных лиц")
    private List<InsuredPersonTable> insuredPersons;

    @Schema(description = "Сеть медицинских учреждений")
    private List<MedicalInstitution> medicalInstitutions;
}
