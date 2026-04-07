package ru.astondevs.mycare.mapper;

import java.util.List;
import java.util.Locale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.astondevs.mycare.dto.policy.request.CreatePolicyRequest;
import ru.astondevs.mycare.dto.policy.response.DmsPolicyDetailResponse;
import ru.astondevs.mycare.dto.policy.response.PolicyResponse;
import ru.astondevs.mycare.dto.policy.response.policydetail.Address;
import ru.astondevs.mycare.dto.policy.response.policydetail.CostInfo;
import ru.astondevs.mycare.dto.policy.response.policydetail.InsuredPersonTable;
import ru.astondevs.mycare.dto.policy.response.policydetail.MainPolicyInfo;
import ru.astondevs.mycare.dto.policy.response.policydetail.MedicalInstitution;
import ru.astondevs.mycare.dto.policy.response.policydetail.PassportData;
import ru.astondevs.mycare.dto.policy.response.policydetail.PersonalData;
import ru.astondevs.mycare.dto.policy.response.policydetail.Policyholder;
import ru.astondevs.mycare.models.entity.Client;
import ru.astondevs.mycare.models.entity.Policy;

/**
 * Маппер для преобразования между DTO и Entity {@link Policy}.
 * <p>
 * Адаптирован для работы с паттерном Assigned Identity (UUID v7).
 *
 * @author Mikhail Ermakov
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PolicyMapper {

    /**
     * Преобразует DTO для создания в сущность {@link Policy}.
     * <p>
     * <b>Особенности маппинга:</b>
     * <ul>
     * <li>{@code policyId}: Игнорируется, чтобы использовалось значение {@code UuidPro.nextV7()},
     * инициализированное в сущности (через @Builder.Default).</li>
     * <li>{@code isNew}: Служебное поле Persistable, игнорируется.</li>
     * <li>Системные поля (createdAt, updatedAt, version) управляются JPA/Auditing.</li>
     * </ul>
     *
     * @param request DTO с данными для создания.
     * @return Новая сущность с предустановленным ID.
     */
    @Mapping(target = "insuranceContract.insuranceContractId", source = "insuranceContractId")
    @Mapping(target = "policyId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isNew", ignore = true)
    Policy toEntity(CreatePolicyRequest request);

    /**
     * Преобразует сущность в DTO.
     *
     * @param policy Сущность для преобразования.
     * @return DTO {@link PolicyResponse}, заполненная данными из сущности.
     */
    @Mapping(target = "dmsType", source = "insuranceContract.insuranceApplication.dmsProgram.dmsType")
    @Mapping(target = "clientId", source = "insuranceContract.insuranceApplication.clientId")
    @Mapping(target = "clientType", source = "insuranceContract.insuranceApplication.clientType")
    PolicyResponse toDto(Policy policy);

    /**
     * Преобразует список сущностей в список DTO.
     *
     * @param policies Список сущностей для преобразования.
     * @return Список DTO {@link PolicyResponse}.
     */
    List<PolicyResponse> toDtoList(List<Policy> policies);

    /**
     * Преобразует сущность {@link Policy} в полный DTO {@link DmsPolicyDetailResponse}.
     * <p>
     * Выполняет маппинг всех вложенных структур: - Основной информации о полисе - Данных
     * страхователя (личные данные, паспорт, адреса) - Списка застрахованных лиц - Сети доступных
     * медицинских учреждений
     *
     * @param policy Сущность {@link Policy} из БД
     * @return DTO {@link DmsPolicyDetailResponse} с полной информацией о полисе
     * @since 1.0.0
     */
    @Mapping(source = "policy", target = "mainInfo", qualifiedByName = "policyToMainPolicyInfo")
    @Mapping(source = "policy", target = "policyholder", qualifiedByName = "policyToPolicyholder")
    @Mapping(target = "insuredPersons", expression = "java(java.util.Collections.emptyList())")
    @Mapping(target = "medicalInstitutions", expression = "java(java.util.Collections.emptyList())")
    DmsPolicyDetailResponse toDmsPolicyDetailResponse(Policy policy);

    /**
     * Преобразует {@link Policy} в {@link MainPolicyInfo}.
     *
     * @param policy Сущность полиса
     * @return Основная информация о полисе
     */
    @Named("policyToMainPolicyInfo")
    @Mapping(source = "policyId", target = "policyNumber", qualifiedByName = "formatPolicyNumber")
    @Mapping(source = "status", target = "status", qualifiedByName = "policyStatusToString")
    @Mapping(source = "insuranceContract.insuranceApplication.dmsProgram.dmsType",
        target = "programType", qualifiedByName = "programTypeToString")
    @Mapping(source = "policy", target = "availableServices", qualifiedByName = "buildAvailableServices")
    @Mapping(source = "startDate", target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "endDate", target = "endDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "policy", target = "cost", qualifiedByName = "policyCost")
    @Mapping(source = "startDate", target = "createdAt", dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    MainPolicyInfo toMainPolicyInfo(Policy policy);

    /**
     * Преобразует {@link Policy} в {@link Policyholder} с данными страхователя.
     * <p>
     * Маппирует личные данные из связанного {@link Client}. Паспортные данные и адреса используют
     * заглушки (т.к. их нет в текущей БД).
     *
     * @param policy Сущность полиса
     * @return Информация о страхователе
     */
    @Named("policyToPolicyholder")
    @Mapping(source = "insuranceContract.insuranceApplication.client", target = "personalData")
    @Mapping(target = "passportData", expression = "java(buildPassportDataStub())")
    @Mapping(target = "registrationAddress", expression = "java(buildAddressStub())")
    @Mapping(target = "residentialAddress", expression = "java(buildAddressStub())")
    Policyholder toPolicyholder(Policy policy);

    /**
     * Преобразует стоимость полиса в {@link CostInfo}.
     *
     * @param policy Сущность полиса
     * @return Информация о стоимости
     */
    @Named("policyCost")
    @Mapping(source = "insuranceContract.totalAmount", target = "amount")
    @Mapping(target = "currency", constant = "RUB")
    @Mapping(source = "insuranceContract.totalAmount", target = "formatted", qualifiedByName = "formatCost")
    CostInfo toCostInfo(Policy policy);

    /**
     * Преобразует {@link Client} в {@link PersonalData}.
     *
     * @param client Клиент
     * @return Личные данные
     */
    PersonalData toPersonalData(Client client);

    /**
     * Преобразует застрахованное лицо в {@link InsuredPersonTable}.
     *
     * @param insuredPerson Застрахованное лицо из сущности
     * @return DTO застрахованного лица
     */
    InsuredPersonTable toInsuredPersonTable(
        ru.astondevs.mycare.models.entity.InsuredPerson insuredPerson);

    /**
     * Преобразует медицинское учреждение в {@link MedicalInstitution}.
     *
     * @param facility Медицинское учреждение из сущности
     * @return DTO медицинского учреждения
     */
    MedicalInstitution toMedicalInstitution(
        ru.astondevs.mycare.models.entity.MedicalFacility facility);

    /**
     * Форматирует ID полиса в номер полиса. Преобразует UUID в строку формата "BB-DMS-XXXXXXXXX".
     *
     * @param policyId UUID полиса
     * @return Отформатированный номер полиса
     */
    @Named("formatPolicyNumber")
    default String formatPolicyNumber(java.util.UUID policyId) {
        if (policyId == null) {
            return null;
        }
        String cleaned = policyId.toString().replace("-", "");
        return "BB-DMS-" + cleaned.substring(0, 9).toUpperCase();
    }

    /**
     * Преобразует статус полиса в строку (lowercase).
     *
     * @param status Статус полиса
     * @return Строковое представление статуса
     */
    @Named("policyStatusToString")
    default String policyStatusToString(ru.astondevs.mycare.models.enums.PolicyStatus status) {
        if (status == null) {
            return null;
        }
        return status.toString().toLowerCase();
    }

    /**
     * Преобразует тип программы в строку (lowercase).
     *
     * @param dmsType Тип программы ДМС
     * @return Строковое представление типа программы
     */
    @Named("programTypeToString")
    default String programTypeToString(ru.astondevs.mycare.models.enums.DmsType dmsType) {
        if (dmsType == null) {
            return null;
        }
        return dmsType.toString().toLowerCase();
    }

    /**
     * Форматирует стоимость полиса с разделением тысяч и символом валюты.
     *
     * @param cost Стоимость полиса (BigDecimal)
     * @return Отформатированная стоимость с валютой
     */
    @Named("formatCost")
    default String formatCost(java.math.BigDecimal cost) {
        if (cost == null) {
            return null;
        }
        String formatted = String.format(Locale.US, "%,.2f", cost.doubleValue());
        formatted = formatted.replace(",", " ").replace(".", ",");
        return formatted + " ₽";
    }

    /**
     * Строит список доступных услуг на основе DmsProgram.
     * <p>
     * TODO: После реализации таблицы services связать через many-to-many.
     *
     * @param policy Сущность полиса
     * @return Список доступных услуг
     */
    @Named("buildAvailableServices")
    default java.util.List<String> buildAvailableServices(Policy policy) {
        java.util.List<String> services = new java.util.ArrayList<>();
        if (policy == null ||
            policy.getInsuranceContract() == null ||
            policy.getInsuranceContract().getInsuranceApplication() == null ||
            policy.getInsuranceContract().getInsuranceApplication().getDmsProgram() == null) {
            return services;
        }
        ru.astondevs.mycare.models.entity.DmsProgram program = policy.getInsuranceContract()
            .getInsuranceApplication()
            .getDmsProgram();

        if (program.isClinicService()) {
            services.add("Поликлиническое обслуживание");
        }
        if (program.isHospitalization()) {
            services.add("Экстренная госпитализация");
        }
        if (program.isStomatology()) {
            services.add("Стоматологическое обслуживание");
        }
        if (program.isAmbulance()) {
            services.add("Скорая медицинская помощь");
        }
        if (program.isCallingDoctor()) {
            services.add("Вызов врача на дом");
        }
        if (program.isTelemedicine()) {
            services.add("Телемедицина");
        }

        return services;
    }

    /**
     * Строит заглушку {@link PassportData} с пустыми данными.
     * <p>
     * TODO: Удалить эту заглушку после реализации PassportData в БД.
     * Будет заменена на реальный маппинг из сущности.
     *
     * @return DTO паспортных данных с пустыми значениями
     */
    @Named("buildPassportDataStub")
    default PassportData buildPassportDataStub() {
        return PassportData.builder()
            .series(null)
            .number(null)
            .departmentCode(null)
            .department(null)
            .issueDate(null)
            .build();
    }

    /**
     * Строит заглушку {@link Address} с пустыми данными.
     * <p>
     * TODO: Удалить эту заглушку после реализации Address в БД.
     * Будет заменена на реальный маппинг из сущности.
     *
     * @return DTO адреса с пустыми значениями
     */
    @Named("buildAddressStub")
    default Address buildAddressStub() {
        return Address.builder()
            .country(null)
            .region(null)
            .district(null)
            .city(null)
            .street(null)
            .houseNumber(null)
            .buildingNumber(null)
            .porchNumber(null)
            .flatNumber(null)
            .roomNumber(null)
            .postalCode(null)
            .build();
    }
}