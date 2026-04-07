package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PolicyStatus {
    ACTIVE("Активен"),
    INACTIVE("Неактивен");

    private final String description;
}
