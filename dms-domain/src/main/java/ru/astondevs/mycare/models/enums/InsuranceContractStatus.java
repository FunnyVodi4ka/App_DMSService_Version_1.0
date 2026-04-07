package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InsuranceContractStatus {
    PENDING("в ожидании"),
    SIGNED("подписан"),
    PAID("оплачен"),
    CANCELLED("отменено"),
    EXPIRED("истек срок");

    private final String description;
}
