package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import ru.astondevs.mycare.models.enums.PolicyStatus;
import ru.astondevs.mycare.uuid.core.UuidPro;

/**
 * Сущность, представляющая страховой полис.
 * <p>
 * Реализует паттерн <b>Assigned Identity</b> с использованием UUID v7,
 * что позволяет генерировать идентификатор на стороне приложения до сохранения в БД
 * и избегать лишних SELECT-запросов при вставке.
 *
 * @author Ivan Sergienko
 * @version 2.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "policies", schema = "dms_service")
@EqualsAndHashCode(of = "policyId")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Policy implements Persistable<UUID> {

    /**
     * Служебный флаг для управления жизненным циклом (New vs Detached).
     * Необходим для корректной работы {@link Persistable}.
     * Инициализируется как {@code true}, чтобы форсировать INSERT для новых объектов.
     */
    @Transient
    @Builder.Default
    private boolean isNew = true;

    /**
     * Возвращает уникальный идентификатор сущности.
     * <p>
     * Реализация контракта {@link Persistable}.
     *
     * @return UUID идентификатор.
     */
    @Nullable
    @Override
    public UUID getId() {
        return policyId;
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
     * Уникальный идентификатор полиса.
     * <p>
     * Инициализируется значением UUID v7 по умолчанию через {@link UuidPro#nextV7()}.
     * Аннотация {@code @Builder.Default} гарантирует сохранение значения при использовании билдера.
     */
    @Id
    @Column(name = "policy_id", nullable = false, updatable = false)
    @Builder.Default
    private UUID policyId = UuidPro.nextV7();

    /**
     * Версия сущности для реализации оптимистической блокировки.
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Контракт страхования
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_contract_id", nullable = false)
    private InsuranceContract insuranceContract;

    /**
     * Статус полиса
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PolicyStatus status;

    /**
     * Дата начала действия полиса Дата и время создания записи (в UTC).
     * <p>
     * Заполняется автоматически при первой вставке (persist) с помощью
     * {@link AuditingEntityListener}. Поле не обновляемое.
     */
    @CreatedDate
    @Column(name = "start_date", nullable = false, updatable = false)
    private Instant startDate;

    /**
     * Дата окончания действия полиса
     */
    @Column(name = "end_date", nullable = false, updatable = false)
    private Instant endDate;

    /**
     * Номер полиса
     */
    @Column(name = "number", nullable = false, length = 50)
    private String number;

    /**
     * URL документа
     */
    @Column(name = "policy_url", nullable = false, length = 255)
    private String policyUrl;

    /**
     * Дата и время последнего обновления записи (в UTC).
     * <p>
     * Заполняется (и обновляется) автоматически при операциях persist и merge с помощью
     * {@link AuditingEntityListener}.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    private void calculateEndDate() {
        if (this.endDate == null && this.startDate != null) {
            this.endDate = this.startDate.plus(365, ChronoUnit.DAYS);
        }
    }
}
