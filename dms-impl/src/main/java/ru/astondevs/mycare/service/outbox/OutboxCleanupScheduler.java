package ru.astondevs.mycare.service.outbox;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.astondevs.mycare.repository.OutboxRepository;

/**
 * Фоновый сервис (Scheduler) для очистки таблицы 'outbox'.
 * <p>
 * Использует Shedlock для гарантии эксклюзивного
 * выполнения в кластерной среде.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxCleanupScheduler {

    /**
     * Размер пакета для удаления записей за один запрос.
     * Выбрано небольшое значение (500),
     * чтобы минимизировать время блокировки таблицы.
     */
    private static final int BATCH_SIZE = 500;

    /**
     * Срок хранения обработанных (SENT) событий (в днях).
     * События хранятся 7 дней для аудита и
     * предоставления времени на восстановление.
     */
    private static final int RETENTION_DAYS = 7;

    /**
     * Репозиторий для доступа к таблице 'outbox'.
     */
    private final OutboxRepository outboxRepository;

    /**
     * Запускается ежедневно в 3:00 AM по времени сервера.
     * <p>
     * Использует {@link SchedulerLock} для гарантии,
     * что задача будет выполнена только на одном
     * инстансе сервиса в кластере.
     * <p>
     * Удаляет старые, подтвержденные ('SENT') события
     * пакетами (batch) в цикле,
     * пока не будут удалены все подходящие записи.
     */
    @Scheduled(cron = "0 00 03 * * ?")
    @SchedulerLock(
        name = "OutboxCleanupScheduler_cleanProcessedEvents",
        lockAtMostFor = "${scheduler.lock.at-most-for}",
        lockAtLeastFor = "${scheduler.lock.at-least-for}"
    )
    public void cleanProcessedEvents() {
        log.info(
            "Starting Outbox cleanup task.",
            keyValue("retentionDays", RETENTION_DAYS),
            keyValue("batchSize", BATCH_SIZE)
        );

        Instant retentionDate = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
        int totalDeleted = 0;
        int deletedInBatch;

        try {
            do {
                deletedInBatch = outboxRepository.deleteSentEventsOlderThan(
                    retentionDate,
                    BATCH_SIZE
                );
                totalDeleted += deletedInBatch;

            } while (deletedInBatch == BATCH_SIZE);

        } catch (Exception e) {
            log.error(
                "Error during Outbox cleanup task. Task will be retried on next schedule.",
                keyValue("totalDeletedSoFar", totalDeleted),
                keyValue("errorMessage", e.getMessage()),
                e
            );
        }
    }
}
