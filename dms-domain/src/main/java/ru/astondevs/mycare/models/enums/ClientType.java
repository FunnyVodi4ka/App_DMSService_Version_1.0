package ru.astondevs.mycare.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление, определяющее тип клиента.
 * <p>
 * Используется для разграничения прав и логики обработки
 * заявлений для разных категорий клиентов.
 *
 * @author Ivan Sergienko
 * @version 1.1
 */
@RequiredArgsConstructor
@Getter
public enum ClientType {

    /**
     * Физическое лицо.
     */
    INDIVIDUAL("Физическое лицо"),

    /**
     * Представитель компании.
     */
    REPRESENTATIVE("Сотрудник компании"),

    /**
     * Представитель компании.
     */
    REPRESENTATIVE_ADMIN("Представитель компании");

    /**
     * Описание типа клиента.
     */
    private final String description;
}
