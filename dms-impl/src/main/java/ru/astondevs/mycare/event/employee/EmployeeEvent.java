package ru.astondevs.mycare.event.employee;

import java.util.UUID;
import ru.astondevs.mycare.models.enums.EmployeeRole;

/**
 * Единый ивент изменения состояния сотрудника. Подходит и для создания, и для обновления.
 * <p>
 *
 * @param eventId    Уникальный идентификатор сотрудника.
 * @param employeeId Уникальный идентификатор сотрудника.
 * @param lastName   Фамилия сотрудника.
 * @param firstName  Имя сотрудника.
 * @param middleName Отчество сотрудника (может быть {@code null}).
 * @param managerId  Уникальный идентификатор менеджера.
 */
public record EmployeeEvent(
	UUID eventId,
	UUID employeeId,
	EmployeeRole employeeRole,
	String lastName,
	String firstName,
	String middleName,
	UUID managerId
) {

}