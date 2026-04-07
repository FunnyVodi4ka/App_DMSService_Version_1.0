package ru.astondevs.mycare.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип пакета ДМС
 *
 * @author Filipp Stavcev
 */
@Getter
@AllArgsConstructor
public enum DmsType {

    BASIC("Базовый"),
    COMPREHENSIVE("Полный"),
    MAXIMUM("Максимальный");

    private final String description;
}
