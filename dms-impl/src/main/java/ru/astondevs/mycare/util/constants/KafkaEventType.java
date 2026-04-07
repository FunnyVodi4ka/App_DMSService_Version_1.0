package ru.astondevs.mycare.util.constants;


import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление типов событий, получаемых из Kafka.
 * <p>
 * Используется для идентификации операций над сущностями (создание, обновление, удаление)
 * на основе значения заголовка {@code EVENT_TYPE} в сообщениях Kafka.
 * Каждая константа связана со строковым описанием, которое ожидается во внешнем событии.
 * </p>
 * *
 *
 * @author Ivan Sakharov
 * @since 1/26/2026
 */
@Getter
@RequiredArgsConstructor
public enum KafkaEventType {
    /**
     * Событие создания новой сущности.
     */
    CREATE("CREATE"),

    /**
     * Событие обновления существующей сущности.
     */
    UPDATE("UPDATE"),

    /**
     * Событие удаления сущности.
     */
    DELETE("DELETE");

    /**
     * Строковое описание типа события, используемое во внешних системах и заголовках Kafka.
     */
    private final String description;

    /**
     * Выполняет поиск типа события по его строковому описанию.
     * <p>
     * Поиск осуществляется без учета регистра (case-insensitive). Это позволяет гибко сопоставлять
     * значения, приходящие в заголовках Kafka, с константами перечисления.
     * </p>
     *
     * @param description строковое представление типа события (например, "CREATE" или "UPDATE").
     * @return {@link Optional}, содержащий найденный {@link KafkaEventType},
     * или пустой {@link Optional}, если совпадений не найдено или передан {@code null}.
     */
    public static Optional<KafkaEventType> fromDescription(String description) {
        return Arrays.stream(values())
            .filter(type -> type.description.equalsIgnoreCase(description))
            .findFirst();
    }
}
