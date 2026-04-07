package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MedicalFacilitiesType {
    CLINIC("клиника"),
    HOSPITAL("больница"),
    MEDICAL_CENTER("медицинский центр"),
    DENTISTRY("стоматология"),
    LABORATORY("лаборатория");

    private final String description;
}
