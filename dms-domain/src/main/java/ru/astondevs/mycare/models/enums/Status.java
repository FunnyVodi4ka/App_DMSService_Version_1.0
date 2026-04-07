package ru.astondevs.mycare.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Статус поданной заявки
 *
 * @author Victor Vityushov
 */
@Getter
@AllArgsConstructor
public enum Status {

    PENDING("Рассматривается"),
    DECLINED("Отклонена"),
    APPROVED("Одобрена");

    private final String description;
}
