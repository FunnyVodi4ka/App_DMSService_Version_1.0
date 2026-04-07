package ru.astondevs.mycare.util.kafka;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

/**
 * Служебный класс для безопасного извлечения
 * и парсинга заголовков из {@link ConsumerRecord}.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class ConsumerRecordHeaderUtils {

    /**
     * Вспомогательный метод для безопасного
     * извлечения заголовка Kafka в виде String.
     *
     * @param record Запись Kafka (сообщение).
     * @param key    Имя искомого заголовка.
     * @return Значение заголовка в виде String
     * или {@code null}, если не найдено.
     */
    public static String extractHeaderAsString(ConsumerRecord<?, ?> record, String key) {
        if (record == null || key == null) {
            return null;
        }

        Header header = record.headers().lastHeader(key);

        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Вспомогательный метод для безопасного
     * извлечения заголовка Kafka в виде UUID.
     *
     * @param record Запись Kafka (сообщение).
     * @param key    Имя искомого заголовка.
     * @return Значение заголовка в виде UUID
     * или {@code null}, если не найдено или
     * не удалось распарсить.
     */
    public static UUID extractHeaderAsUuid(ConsumerRecord<?, ?> record, String key) {

        String value = extractHeaderAsString(record, key);

        if (value == null) {
            return null;
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            log.warn(
                "Failed to parse header as UUID.",
                keyValue("headerKey", key),
                keyValue("headerValue", value)
            );
            return null;
        }
    }
}
