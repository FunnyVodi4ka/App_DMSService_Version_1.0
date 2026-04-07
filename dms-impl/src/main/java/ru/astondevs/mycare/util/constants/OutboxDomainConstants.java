package ru.astondevs.mycare.util.constants;

import lombok.experimental.UtilityClass;

/**
 * Внутренние константы домена Outbox.
 * <p>
 * Класс инкапсулирует настройки бизнес-логики, ограничения и классификаторы
 * ошибок, специфичные исключительно для жизненного цикла событий Outbox.
 *
 * @author Ivan Sergienko
 * @since 1.4
 */
@UtilityClass
public class OutboxDomainConstants {

    /**
     * Максимально допустимая длина Stack Trace для сохранения в колонку last_error.
     * <p>
     * Лимит в 500 символов установлен для предотвращения чрезмерного раздувания
     * размера БД при сохранении объемных диагностических данных.
     */
    public static final int MAX_STACK_TRACE_LENGTH = 500;

    /**
     * Строковая заглушка для обозначения неопределенного или отсутствующего значения.
     * <p>
     * Применяется в ситуациях, когда обязательное мета-поле не может быть извлечено,
     * чтобы избежать использования null в системах аналитики.
     */
    public static final String UNKNOWN_VALUE = "unknown";

    /**
     * Строковая константа для извлечения типа события из Headers kafka event.
     */
    public static final String EVENT_TYPE = "eventType";

    /**
     * Классификатор причин возникновения ошибок при обработке событий.
     * <p>
     * Используется для типизации исключений в логах и метриках.
     *
     * @author Ivan Sergienko
     * @since 1.4
     */
    @UtilityClass
    public static class ErrorReasons {

        /**
         * Указывает на логическую ошибку в структуре сообщения (poison pill).
         * Сообщение синтаксически корректно, но не может быть обработано из-за
         * противоречия бизнес-правилам.
         */
        public static final String POISON_PILL = "PoisonPill";

        /**
         * Указывает на нарушение контракта передачи метаданных.
         * <p>
         * Возникает, когда в сообщении Kafka отсутствуют обязательные заголовки
         * (например, идентификатор трассировки или идентификатор корреляции),
         * без которых дальнейшая обработка невозможна.
         */
        public static final String MISSING_HEADER = "MissingHeaderError";

        /**
         * Указывает на нарушение целостности данных между Kafka и БД.
         * Ситуация, при которой событие существует в Kafka, но запись о нем
         * в таблице outbox отсутствует или повреждена.
         */
        public static final String DB_INCONSISTENCY = "db_inconsistency";

        /**
         * Общая категория для непредвиденных исключений (Runtime Exceptions).
         * Используется для группировки ошибок, не имеющих специфической бизнес-категории.
         */
        public static final String UNEXPECTED = "unexpected_exception";
    }
}
