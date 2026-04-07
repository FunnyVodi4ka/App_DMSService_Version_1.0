package ru.astondevs.mycare.util.constants;

import lombok.AllArgsConstructor;

/**
 * Перечисление с текстовыми константами
 *
 * @author Filipp Stavcev
 */
@AllArgsConstructor
public enum TextConstants {

    LIST_OF_DMS_INSURANCES_NOT_FOUND("Список заявок не найден"),
    AGREEMENT_NOT_FOUND("Полис страхования не найден"),
    DMS_INSURANCE_NOT_FOUND("Заявка не найдена"),
    FAILED_TO_SAVE("Failed to save"),
    FAILED_REJECT_MASSAGE("При отклонении заявки требуется написать причину"),
    DMS_REVIEW_SUCCESS("Заявка рассмотрена. Запись успешно обновлена в базе данных"),
    STATUS_INSURANCE_EXIST("Статус страхования уже имеет значение: "),
    CLIENT_CREATING_OPERATION_NAME("creating client"),
    CLIENT_CHANGING_OPERATION_NAME("changing client"),
    ;



    private final String description;

    public String get() {
        return description;
    }
}
