package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление, определяющее возможные статусы (этапы жизненного цикла)
 * заявления на страховой полис.
 *
 * @author Ivan Sergienko
 * @version 1.1
 */
@Getter
@RequiredArgsConstructor
public enum InsuranceApplicationStatus {

    /**
     * Заявление создано и ожидает обработки.
     */
    PENDING("в ожидании"),

    /**
     * Заявление проверено и одобрено.
     */
    APPROVED("одобрено"),

    /**
     * Заявление отменено.
     */
    CANCELLED("отменено"),

    /**
     * Заявление проверено и отклонено.
     */
    REJECTED("отклонено");

    /**
     * Описание статуса.
     */
    private final String description;
}
