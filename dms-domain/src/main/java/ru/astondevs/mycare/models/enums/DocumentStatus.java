package ru.astondevs.mycare.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentStatus {
	
	ACTUAL("Актуальный"),
	
	REQUIRES_UPDATE("Требует обновления"),
	
	EXPIRED("Истек срок действия");
	
	private final String description;
}