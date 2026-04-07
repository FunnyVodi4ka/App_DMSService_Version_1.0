package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.astondevs.mycare.models.enums.EmployeeRole;

/**
 * Сущность, представляющая сотрудника в системе.
 * <p>
 * Данный класс является локальной проекцией данных из внешнего "Сервиса сотрудников". Актуализация
 * данных происходит асинхронно через события, получаемые из Apache Kafka.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "employeeId")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "employees", schema = "dms_service")
@FieldNameConstants
public class Employee {
	
	/**
	 * Уникальный идентификатор сотрудника.
	 * <p>
	 * Соответствует идентификатору в мастер-системе (Сервис сотрудников). Значение приходит из
	 * Kafka и не генерируется в текущем сервисе.
	 * </p>
	 */
	@Id
	@Column(name = "id")
	private UUID employeeId;
	
	@Column(name = "employee_roles", nullable = false)
	private EmployeeRole employeeRole;
	
	/**
	 * Фамилия сотрудника.
	 */
	@Column(name = "last_name", nullable = false, length = 40)
	private String lastName;
	
	/**
	 * Имя сотрудника.
	 */
	@Column(name = "first_name", nullable = false, length = 40)
	private String firstName;
	
	/**
	 * Отчество сотрудника.
	 */
	@Column(name = "middle_name", nullable = true, length = 40)
	private String middleName;
	
	/**
	 * Идентификатор руководителя.
	 * <p>
	 * Ссылается на {@code id} записи в текущей таблице, которая также синхронизируется из внешнего
	 * сервиса.
	 */
	@Column(name = "manager_id")
	private UUID managerId;

	/**
	 * Дата и время создания записи.
	 * <p>
	 * Поле заполняется автоматически благодаря аннотации {@link CreatedDate} и слушателю
	 * {@link AuditingEntityListener}.
	 * </p>
	 */
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	/**
	 * Дата и время последнего обновления записи.
	 * <p>
	 * Поле обновляется автоматически при каждом сохранении сущности благодаря аннотации
	 * {@link LastModifiedDate} и слушателю {@link AuditingEntityListener}.
	 * </p>
	 */
	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;
}
