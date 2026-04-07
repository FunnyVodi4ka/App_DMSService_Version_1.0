package ru.astondevs.mycare.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.astondevs.mycare.event.employee.EmployeeEvent;
import ru.astondevs.mycare.models.entity.Employee;

/**
 * Маппер для преобразования между DTO и Entity {@link Employee}.
 * <p>
 * Использует MapStruct для генерации реализации, обеспечивая безопасность типов и высокую
 * производительность.
 *
 * @author Mikhail Ermakov
 * @since 12/11/2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {
	
	/**
	 * Преобразует событие (DTO) в сущность.
	 * <p>
	 * Поля с одинаковыми именами (employeeId, firstName, lastName, etc.) маппятся автоматически.
	 * Поля createdAt/updatedAt будут проигнорированы.
	 * </p>
	 */
	@Mapping(target = "employeeId", source = "employeeId")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Employee createEntityFromEvent(EmployeeEvent employeeEvent);
	
	/**
	 * Обновляет поля уже существующей сущности.
	 * <p>
	 * Поля с одинаковыми именами (employeeId, firstName, lastName, etc.) обновляются автоматически.
	 * Поля createdAt/updatedAt будут проигнорированы.
	 * </p>
	 */
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	void updateEntityFromEvent(EmployeeEvent event, @MappingTarget Employee employee);
}
