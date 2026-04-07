package ru.astondevs.mycare.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmployeeRole {
	INSURANCE_SPECIALIST_IC("Специалист страховой компании MyCare по работе с физ. лицами"),
	INSURANCE_SPECIALIST_CC("Специалист страховой компании MyCare по работе с юр лицами"),
	INSURANCE_CLAIM_SPECIALIST_IC(
		"Специалист страховой компании MyCare по работе с выплатами физ. лиц"),
	INSURANCE_CLAIM_SPECIALIST_CC(
		"Специалист страховой компании MyCare по работе с выплатами юр. лиц"),
	INSURANCE_MANAGER("Главный менеджер (руководитель специалистов страховой компании)"),
	INSURANCE_MODERATOR(
		"Модератор сайта страховой, модерирует: отзывы," +
		"наполняет сайт новостями промо акциями, добавляет офисы на карты, блокирует пользователей"),
	OPERATIONS_SPECIALIST("Операционист"),
	CREDIT_MANAGER("Менеджер по кредитам"),
	VIP_CLIENT_MANAGER("Менеджер по работе с VIP-клиентами"),
	SECURITY_AML_OFFICER("Сотрудник отдела безопасности и AML"),
	BANK_MANAGER("Главный менеджер (руководитель специалистов банка)"),
	BANK_ADMINISTRATOR("Администратор банка"),
	MY_CARE_ADMIN(
		"Администратор системы MyCare, отвечает за работу сайта," +
		"добавляет пользователя в кейклок (другая авторизация). Общий для страховой и банка");
	
	private final String description;
}