package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimApplicationStatus {
    PENDING("В обработке"),
    APPROVED("Одобрено"),
    CANCELLED("Отменено"),
    REJECTED("Отклонено");

    private final String description;
}
