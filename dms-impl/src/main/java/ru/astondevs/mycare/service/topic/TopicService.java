package ru.astondevs.mycare.service.topic;

import ru.astondevs.mycare.kafkastarter.properties.KafkaStarterProperties;
import ru.astondevs.mycare.exception.kafka.TopicConfigurationNotFoundException;

/**
 * Интерфейс сервиса для работы с конфигурацией топиков Kafka.
 * <p>
 * Абстрагирует логику поиска и валидации имен топиков,
 * определенных в {@link KafkaStarterProperties}.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
public interface TopicService {

    /**
     * Ищет имя топика в конфигурации Kafka-стартера
     * по его ожидаемому имени.
     * <p>
     * Выбрасывает {@link TopicConfigurationNotFoundException},
     * если топик не найден в конфигурации,
     * что обеспечивает "fail-fast" поведение при
     * запуске приложения.
     *
     * @param kafkaProperties Объект {@link KafkaStarterProperties},
     * содержащий список настроенных топиков.
     * @param expectedName    Ожидаемое имя топика (e.g.,
     * "insurance.dms-insurance-applications-created-event").
     * @return Найденное имя топика.
     * @throws TopicConfigurationNotFoundException если
     * {@code kafkaProperties} или список топиков
     * равны {@code null}, или если топик
     * с {@code expectedName} не найден.
     */
    String findTopicNameOrFail(
        KafkaStarterProperties kafkaProperties,
        String expectedName);
}
