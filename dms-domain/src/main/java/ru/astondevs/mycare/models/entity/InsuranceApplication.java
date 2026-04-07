package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import ru.astondevs.mycare.models.enums.ClientType;
import ru.astondevs.mycare.models.enums.InsuranceApplicationStatus;
import ru.astondevs.mycare.uuid.core.UuidPro;

/**
 * Сущность, представляющая заявление на страховой полис ДМС.
 * <p>
 * Хранит всю информацию о заявлении, включая его статус, связь с программой страхования и
 * идентификаторы связанных сторон.
 *
 * @author Ivan Sergienko
 * @version 2.0
 */
@Entity
@Table(name = "insurance_applications", schema = "dms_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "insuranceApplicationId")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class InsuranceApplication implements Persistable<UUID> {

    /**
     * Служебный флаг для управления жизненным циклом (New vs Detached). Необходим для корректной
     * работы {@link Persistable}. Инициализируется как {@code true}, так как при создании нового
     * объекта мы хотим форсировать INSERT, даже если ID уже задан.
     */
    @Transient
    @Builder.Default
    private boolean isNew = true;

    /**
     * Возвращает уникальный идентификатор сущности.
     * <p>
     * Реализация контракта {@link Persistable}. Метод возвращает значение поля
     * {@code insuranceApplicationId}, которое инициализируется (Eagerly) при создании объекта.
     *
     * @return UUID идентификатор.
     */
    @Nullable
    @Override
    public UUID getId() {
        return insuranceApplicationId;
    }

    /**
     * Возвращает признак новизны сущности.
     * <p>
     * Используется Spring Data JPA для выбора стратегии сохранения:
     * <ul>
     * <li><b>true:</b> Выполняется {@code entityManager.persist()} (INSERT).</li>
     * <li><b>false:</b> Выполняется {@code entityManager.merge()} (SELECT + UPDATE),
     * что для нас нежелательно при вставке.</li>
     * </ul>
     * Благодаря ручному управлению флагом,
     * мы избегаем лишнего SELECT-запроса перед вставкой новой записи с заранее известным ID.
     *
     * @return true, если объект новый и требует INSERT.
     */
    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Сбрасывает флаг новизны.
     * <p>
     * Метод автоматически вызывается JPA-провайдером (Hibernate) в двух случаях:
     * <ol>
     * <li>После успешной загрузки сущности из БД ({@code @PostLoad}).</li>
     * <li>После успешного сохранения новой сущности в БД ({@code @PostPersist}).</li>
     * </ol>
     * Это переводит сущность в состояние "Managed" (существующая).
     */
    @PostLoad
    @PostPersist
    protected void markNotNew() {
        this.isNew = false;
    }

    /**
     * Уникальный идентификатор заявления (Primary Key).
     * <p>
     * Инициализируется значением UUID v7 по умолчанию. Аннотация {@code @Builder.Default}
     * гарантирует, что при создании через билдер (например, в маппере) это значение сохранится,
     * если не будет переопределено явно.
     */
    @Id
    @Column(name = "insurance_application_id", nullable = false, updatable = false)
    @Builder.Default
    private UUID insuranceApplicationId = UuidPro.nextV7();

    /**
     * Версия сущности для реализации оптимистической блокировки.
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Связанная программа ДМС. Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dms_program_id", nullable = false)
    private DmsProgram dmsProgram;

    /**
     * Уникальный идентификатор сотрудника страховой компании, обработавшего заявление.
     */
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    /**
     * Уникальный идентификатор клиента.
     * <p>
     * Используется в маппинге для передачи в DTO и для управления связью с Client.
     */
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    /**
     * Связь на клиента (страхователя).
     * <p>
     * Загружается лениво для оптимизации производительности. Использует column = client_id без
     * insertable/updatable, так как это поле управляется непосредственно через {@code clientId}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, insertable = false, updatable = false)
    private Client client;

    /**
     * Тип клиента (физическое лицо или представитель компании).
     * <p>
     * Хранится в БД в виде строки (EnumType.STRING) для обеспечения обратной совместимости при
     * добавлении новых типов.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    /**
     * Уникальный номер заявления.
     */
    @Column(name = "number", nullable = false, length = 50, unique = true)
    private String insuranceApplicationNumber;

    /**
     * Текущий статус заявления.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InsuranceApplicationStatus insuranceApplicationStatus;

    /**
     * Комментарий к заявлению.
     */
    @Column(name = "comment", length = 400)
    private String comment;

    /**
     * Дата и время создания записи (в UTC).
     * <p>
     * Заполняется автоматически при первой вставке (persist) с помощью
     * {@link AuditingEntityListener}. Поле не обновляемое.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Дата и время последнего обновления записи (в UTC).
     * <p>
     * Заполняется (и обновляется) автоматически при операциях persist и merge с помощью
     * {@link AuditingEntityListener}.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}