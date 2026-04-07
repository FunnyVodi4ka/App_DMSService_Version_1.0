package ru.astondevs.mycare.models.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.astondevs.mycare.models.enums.ClientType;

/**
 * Сущность, представляющая клиента системы.
 * <p>
 * Класс отображается на таблицу {@code client} в схеме {@code dms_service}. Содержит основные
 * персональные данные клиента, его тип, а также служебные поля аудита.
 * </p>
 *
 * @author Ivan Sakharov
 * @since 11/16/2025
 */

@Entity
@Table(name = "clients", schema = "dms_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "clientId")
@EntityListeners(AuditingEntityListener.class)
@FieldNameConstants
public class Client {
	
	/**
	 * Уникальный идентификатор клиента (Primary Key).
	 */
	@Id
	@Column(name = "client_id", nullable = false, updatable = false)
	private UUID clientId;
	
	/**
	 * Тип клиента.
	 * <p>
	 * Хранится в базе данных в виде строки (EnumType.STRING).
	 *
	 * @see ClientType
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "client_type")
	private ClientType clientType;
	
	/**
	 * Имя клиента. Обязательное поле, максимальная длина 100 символов.
	 */
	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;
	
	/**
	 * Фамилия клиента. Обязательное поле, максимальная длина 100 символов.
	 */
	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;
	
	/**
	 * Отчество клиента. Необязательное поле, максимальная длина 100 символов.
	 */
	@Column(name = "middle_name", length = 100)
	private String middleName;
	
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
