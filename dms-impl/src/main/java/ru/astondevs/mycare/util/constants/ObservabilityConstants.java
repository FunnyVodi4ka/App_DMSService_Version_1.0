package ru.astondevs.mycare.util.constants;

import lombok.experimental.UtilityClass;

/**
 * Единый реестр констант для систем наблюдаемости (Logging, Tracing, Metrics).
 * <p>
 * Централизация ключей гарантирует, что одни и те же сущности будут иметь
 * идентичные имена во всех аналитических системах (ELK/Kibana, Grafana, Zipkin/Jaeger).
 * Это позволяет строить сквозные запросы, используя одни и те же фильтры.
 *
 * @author Ivan Sergienko
 * @since 1.4
 */
@UtilityClass
public class ObservabilityConstants {

    /**
     * Ключи контекста и метаданных.
     * <p>
     * Используются в качестве ключей в структурированных логах (keyValue),
     * имен тегов в метриках Micrometer и имен полей Baggage в распределенной трассировке.
     *
     * @author Ivan Sergienko
     * @since 1.4
     */
    @UtilityClass
    public static class Keys {

        /** Имя целевого или исходного топика Kafka. */
        public static final String TOPIC = "topic";

        /** Номер партиции Kafka, в которую записано или из которой прочитано сообщение. */
        public static final String PARTITION = "partition";

        /** Порядковый номер (offset) сообщения внутри партиции Kafka. */
        public static final String OFFSET = "offset";

        /** Уникальный идентификатор бизнес-сущности (агрегата). */
        public static final String AGGREGATE_ID = "aggregateId";

        /** Тип бизнес-сущности (например, InsuranceApplication). */
        public static final String AGGREGATE_TYPE = "aggregateType";

        /** Уникальный идентификатор конкретного события. */
        public static final String EVENT_ID = "eventId";

        /** Технический или бизнес-статус процесса/объекта. */
        public static final String STATUS = "status";

        /** Человекочитаемая причина действия или возникновения ошибки. */
        public static final String REASON = "reason";

        /** Категория или класс возникшей ошибки (например, имя Exception). */
        public static final String ERROR_TYPE = "error_type";

        /** Полный текст сообщения об ошибке. */
        public static final String ERROR_MESSAGE = "errorMessage";

        /** Идентификатор трассировки согласно стандарту W3C Trace Context. */
        public static final String TRACEPARENT = "traceparent";

        /** Сквозной идентификатор корреляции для связи всех логов в рамках одного запроса. */
        public static final String CORRELATION_ID = "correlation-id";

        /** Идентификатор системы или пользователя, инициировавшего цепочку вызовов. */
        public static final String CLIENT_ID = "client-id";

        /** Информация о клиентском приложении (браузер, версия SDK и т.д.). */
        public static final String USER_AGENT = "user-agent";

        /** Сетевой адрес отправителя исходного запроса. */
        public static final String IP_ADDRESS = "ip-address";
    }

    /**
     * Реестр имен метрик для Micrometer.
     * <p>
     * Определяет названия счетчиков и таймеров, используемых
     * для мониторинга производительности и надежности механизма Outbox.
     *
     * @author Ivan Sergienko
     * @since 1.4
     */
    @UtilityClass
    public static class Meters {

        /** Общий префикс для всех метрик модуля Outbox. */
        private static final String PREFIX = "business.outbox.";

        /** Счетчик событий, которые были изолированы из-за критических ошибок ("Poison Pills"). */
        public static final String EVENT_ISOLATED = PREFIX + "event.isolated";

        /** Счетчик успешно подтвержденных событий в БД после отправки в Kafka. */
        public static final String EVENT_ACKNOWLEDGED = PREFIX + "event.acknowledged";

        /** Счетчик системных сбоев при попытке перенаправить сообщение в Dead Letter Topic. */
        public static final String DLT_FAILURE = PREFIX + "dlt.system_failure";

        /** Счетчик ошибок в логике подтверждения событий. */
        public static final String ACK_FAILURE = PREFIX + "ack.failure";
    }
}
