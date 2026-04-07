package ru.astondevs.mycare.util.kafka;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventConverter {

    private final ObjectMapper objectMapper;

    /**
     * Универсальный метод для конвертации payload из Kafka.
     *
     * @param value Объект payload (может быть LinkedHashMap или целевой класс)
     * @param type  Целевой класс события
     * @return Сконвертированный объект или null, если конвертация невозможна
     */
    public <T> T convert(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }

        if (type.isInstance(value)) {
            return type.cast(value);
        } else if (value instanceof LinkedHashMap) {
            try {
                return objectMapper.convertValue(value, type);
            } catch (IllegalArgumentException e) {
                log.error("Failed to convert Map",
                        keyValue("targetType", type.getSimpleName()),
                        keyValue("error", e.getMessage()),
                        e);
                return null;
            }
        } else {
            log.warn("Skipping record with unexpected type",
                    keyValue("actualType", value.getClass().getSimpleName()),
                    keyValue("expectedType", type.getSimpleName()));
            return null;
        }
    }

    /**
     * Десериализует JSON-строку в список объектов указанного типа.
     *
     * @param json        JSON-строка (например, "['field1', 'field2']")
     * @param elementType Класс элементов списка (например, String.class)
     * @return Список объектов или пустой список в случае ошибки/null
     */
    public <T> List<T> convertJsonToList(String json, Class<T> elementType) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON string to List",
                    keyValue("elementType", elementType.getSimpleName()),
                    keyValue("error", e.getMessage()));
            return Collections.emptyList();
        }
    }
}