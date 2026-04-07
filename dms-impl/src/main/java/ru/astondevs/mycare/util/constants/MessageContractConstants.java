package ru.astondevs.mycare.util.constants;

import lombok.experimental.UtilityClass;

/**
 * Реестр констант протокола передачи сообщений через Apache Kafka.
 * <p>
 * Определяет стандартизированные имена заголовков (Headers), обеспечивая
 * технологическую совместимость между компонентами системы: прикладным кодом,
 * Debezium Connect и потребителями (Consumers).
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@UtilityClass
public class MessageContractConstants {

    /**
     * Заголовки прикладного уровня, определяемые бизнес-логикой приложения.
     *
     * @author Ivan Sergienko
     * @since 1.0.0
     */
    @UtilityClass
    public static class Headers {

        /**
         * Уникальный идентификатор записи в таблице outbox.
         * <p>
         * Используется потребителями для обеспечения идемпотентности обработки
         * и обратной связи (Acknowledgment) с базой данных отправителя.
         */
        public static final String OUTBOX_ID = "outbox_id";
    }

    /**
     * Инфраструктурные заголовки, используемые фреймворками и системными компонентами.
     * <p>
     * Включает стандартные ключи для работы с Dead Letter Topics (DLT)
     * и механизмами повторных попыток (Retry).
     *
     * @author Ivan Sergienko
     * @since 1.0.0
     */
    @UtilityClass
    public static class Infrastructure {

        /**
         * Имя исходного топика, в котором возникла ошибка.
         * Автоматически проставляется механизмом Kafka DLT при перекладывании сообщения.
         */
        public static final String DLT_ORIGINAL_TOPIC = "kafka_dlt-original-topic";

        /**
         * Краткое описание исключения, приведшего к обработке сообщения через DLT.
         */
        public static final String DLT_EXCEPTION_MESSAGE = "kafka_dlt-exception-message";

        /**
         * Полный или усеченный стек-трейс ошибки, возникшей на стороне потребителя.
         * Используется для оперативной диагностики без обращения к логам сервиса.
         */
        public static final String DLT_EXCEPTION_STACKTRACE = "kafka_dlt-exception-stacktrace";
    }
}
