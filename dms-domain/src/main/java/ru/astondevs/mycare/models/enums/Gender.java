package ru.astondevs.mycare.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Пол застрахованого лица
 *
 * @author Evgeniy Rusakov
 */
@Getter
@AllArgsConstructor
public enum Gender {

    MALE("мужской"),
    FEMALE("женский");

    private final String description;
}
