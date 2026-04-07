package ru.astondevs.mycare.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import ru.astondevs.mycare.models.enums.OutboxStatus;
import ru.astondevs.mycare.uuid.core.UuidPro;

/**
 * Сущность, представляющая событие для асинхронной отправки (Паттерн Transactional Outbox).
 * <p>
 * Записи в этой таблице создаются в той же транзакции, что и основные
 * бизнес-сущности (например, {@link InsuranceApplication}).
 * <p>
 * Debezium отслеживает INSERT-операции в этой таблице для надежной
 * доставки событий в Kafka.
 *
 * @author Ivan Sergienko
 * @version 2.0
 */
@Entity
@Table(name = "outbox", schema = "dms_service")
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString(of = {"outboxId", "topic", "outboxStatus"})
@EqualsAndHashCode(of = "outboxId")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent implements Persistable<UUID> {

    /**
     * Служебное поле для определения состояния сущности (новая или загруженная).
     * Не сохраняется в базе данных.
     */
    @Transient
    @Builder.Default
    private boolean isNew = true;

    /**
     * Возвращает уникальный идентификатор сущности.
     *
     * @return UUID идентификатор или null.
     */
    @Nullable
    @Override
    public UUID getId() {
        return outboxId;
    }

    /**
     * Возвращает флаг "новизны" сущности.
     * Используется Spring Data JPA для выбора между persist и merge.
     *
     * @return true, если сущность еще не сохранена в БД.
     */
    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Сбрасывает флаг "новизны" после загрузки из БД или сохранения.
     */
    @PostLoad
    @PostPersist
    protected void markNotNew() {
        this.isNew = false;
    }

    /**
     * Уникальный идентификатор события (Primary Key).
     * <p>
     * Использует стратегию генерации UUID v7, обеспечивающую сортировку по времени.
     * Инициализируется значением по умолчанию через {@link UuidPro#nextV7()},
     * что позволяет получить ID до сохранения в БД (Eager Identity).
     */
    @Id
    @Builder.Default
    @Column(name = "outbox_id", nullable = false, updatable = false)
    private UUID outboxId = UuidPro.nextV7();

    /**
     * Идентификатор бизнес-сущности (агрегата), с которой связано событие.
     * <p>
     * Используется в качестве ключа партиционирования Kafka для
     * сохранения порядка сообщений для конкретной сущности.
     */
    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    /**
     * Тип бизнес-сущности (например, "InsuranceApplication").
     * <p>
     * Используется на стороне потребителя для корректной
     * маршрутизации или десериализации.
     */
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Текстовое описание последней ошибки при обработке/доставке.
     * <p>
     * Использует тип TEXT для хранения потенциально длинных stack trace.
     */
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    /**
     * Название топика в Kafka, куда должно быть направлено событие.
     */
    @Column(name = "topic", nullable = false)
    private String topic;

    /**
     * Полезная нагрузка события в формате JSON.
     * <p>
     * Маппинг {@link SqlTypes#JSON} и `columnDefinition = "jsonb"` (для PostgreSQL)
     * является лучшей практикой. Это позволяет БД валидировать JSON
     * и использовать GIN-индексы для поиска по содержимому.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    /**
     * Технический статус доставки события (например, PENDING, SENT, FAILED).
     * <p>
     * Используется для мониторинга и фоновых процессов очистки (cleaner).
     * Используем стандартный маппинг JPA EnumType.STRING для переносимости.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus outboxStatus;

    /**
     * Временная метка в UTC, когда событие было успешно обработано (отправлено).
     * <p>
     * Остается `null` до успешной отправки. Используется
     * фоновыми процессами очистки (cleaner) для удаления
     * старых, успешно отправленных событий.
     */
    @Column(name = "processed_at")
    private Instant processedAt;

    /**
     * Счетчик попыток отправки/обработки.
     * <p>
     * Инициализируется нулем. Используется для
     * реализации стратегий повторных попыток (retry logic)
     * или для переноса "битых" сообщений в FAILED.
     */
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    /**
     * Временная метка создания записи (в UTC).
     * <p>
     * Заполняется автоматически при первой вставке
     * с помощью {@link AuditingEntityListener}.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Заголовки сообщения Kafka в формате JSON.
     * <p>
     * Позволяет передавать метаданные (например, traceId, userId)
     * вместе с основным сообщением (payload), не смешивая их.
     * <p>
     * Маппинг {@link SqlTypes#JSON} и `columnDefinition = "jsonb"`
     * обеспечивает валидацию и возможность индексации в PostgreSQL.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "headers", columnDefinition = "jsonb")
    private String headers;
}
