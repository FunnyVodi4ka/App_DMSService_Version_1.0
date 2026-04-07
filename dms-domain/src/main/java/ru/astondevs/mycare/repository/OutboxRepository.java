package ru.astondevs.mycare.repository;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.mycare.models.entity.OutboxEvent;

/**
 * Репозиторий для доступа к сущностям {@link OutboxEvent}.
 * <p>
 * Содержит кастомные JPQL и Native Query
 * для реализации паттерна Acknowledger/Cleaner.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Атомарно обновляет статус события на SENT по его ID.
     * <p>
     * Вызывается Kafka-консьюмером (Acknowledger)
     * для подтверждения успешной доставки в Kafka.
     * Обновляет только события в статусе PENDING
     * для обеспечения идемпотентности.
     *
     * @param outboxId    ID события (из Kafka-заголовка).
     * @param processedAt Текущее время обработки.
     */
    @Modifying
    @Transactional
    @Query("UPDATE OutboxEvent o " +
           "SET o.outboxStatus = 'SENT', o.processedAt = :processedAt " +
           "WHERE o.outboxId = :outboxId AND o.outboxStatus = 'PENDING'")
    int updateStatusToSent(
        @Param("outboxId") UUID outboxId,
        @Param("processedAt") Instant processedAt
    );

    /**
     * Удаляет N (batchSize) старых,
     * уже обработанных (SENT) событий,
     * которые старше retentionDate.
     * <p>
     * Используется фоновым сервисом (Cleaner).
     * <p>
     * <b>Реализация:</b> Используется Native Query (PostgreSQL)
     * с `LIMIT` для пакетного удаления,
     * чтобы избежать длительных блокировок таблицы `outbox`.
     *
     * @param retentionDate Дата, до которой нужно удалить
     * (e.g., NOW() - 7 days).
     * @param batchSize     Максимальное кол-во записей
     * для удаления за один вызов.
     * @return Количество удаленных записей.
     */
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM dms_service.outbox
        WHERE outbox_id IN (
            SELECT outbox_id
            FROM dms_service.outbox
            WHERE status = 'SENT' AND processed_at < :retentionDate
            LIMIT :batchSize
        )
        """, nativeQuery = true)
    int deleteSentEventsOlderThan(
        @Param("retentionDate") Instant retentionDate,
        @Param("batchSize") int batchSize
    );

    /**
     * Атомарно обновляет статус события на FAILED
     * и записывает текст ошибки.
     * <p>
     * Вызывается DLT-консьюмером для изоляции
     * "отравленного" сообщения (Poison Pill).
     * Обновляет запись независимо от текущего статуса
     * (т.к. FAILED - терминальный статус).
     *
     * @param outboxId     ID события (из Kafka-заголовка).
     * @param errorMessage Текст исключения,
     * приведшего к сбою.
     * @param processedAt  Текущее время обработки сбоя.
     */
    @Modifying
    @Transactional
    @Query("UPDATE OutboxEvent o " +
           "SET o.outboxStatus = 'FAILED', o.lastError = :errorMessage, o.processedAt = :processedAt " +
           "WHERE o.outboxId = :outboxId")
    void updateStatusToFailed(
        @Param("outboxId") UUID outboxId,
        @Param("errorMessage") String errorMessage,
        @Param("processedAt") Instant processedAt
    );
}
