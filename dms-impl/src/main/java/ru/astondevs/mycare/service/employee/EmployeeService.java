package ru.astondevs.mycare.service.employee;


import ru.astondevs.mycare.event.employee.EmployeeEvent;

/**
 * Реализация сервиса для управления жизненным циклом сотрудников. Обеспечивает создание и обновление
 * данных клиента с использованием изолированных транзакций.
 *
 * @author Mikhail Ermakov
 * @since 12/11/2025
 */
public interface EmployeeService {
	
	/**
	 * Обрабатывает событие сотрудника: проверяет наличие в БД. Если сотрудник найден - обновляет
	 * данные. Если не найден - создает нового.
	 */
	void createOrUpdateEmployee(EmployeeEvent event);
	
}
