package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление, описывающее технический статус доставки события из таблицы Outbox.
 *
 * @author Ivan Sergienko
 * @version 1.1
 */
@Getter
@RequiredArgsConstructor
public enum OutboxStatus {

    /**
     * Событие ожидает обработки (отправки). Debezium отслеживает события с этим статусом.
     */
    PENDING("в ожидании"),

    /**
     * Событие было успешно отправлено.
     */
    SENT("отправлено"),

    /**
     * Произошла ошибка при отправке события.
     */
    FAILED("отклонено");

    /**
     * Описание статуса.
     */
    private final String description;
}
