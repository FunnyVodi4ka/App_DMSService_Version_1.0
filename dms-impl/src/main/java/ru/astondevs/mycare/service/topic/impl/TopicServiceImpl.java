package ru.astondevs.mycare.service.topic.impl;

import org.springframework.stereotype.Service;
import ru.astondevs.mycare.exception.kafka.TopicConfigurationNotFoundException;
import ru.astondevs.mycare.kafkastarter.properties.KafkaStarterProperties;
import ru.astondevs.mycare.kafkastarter.properties.topic.TopicProperties;
import ru.astondevs.mycare.service.topic.TopicService;

/**
 * Реализация {@link TopicService}.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
@Service
public class TopicServiceImpl implements TopicService {

    /**
     * {@inheritDoc}
     */
    @Override
    public String findTopicNameOrFail(
        KafkaStarterProperties kafkaProperties,
        String expectedName) {

        if (expectedName == null) {
            throw new TopicConfigurationNotFoundException(
                "Expected topic name must not be null."
            );
        }

        if (kafkaProperties == null || kafkaProperties.getTopics() == null) {
            throw new TopicConfigurationNotFoundException(
                "KafkaStarterProperties is null or getTopics() is null."
            );
        }

        return kafkaProperties.getTopics()
            .stream()
            .filter(t -> t.getName() != null && expectedName.equals(t.getName()))
            .findFirst()
            .map(TopicProperties::getName)
            .orElseThrow(() -> new TopicConfigurationNotFoundException(
                "Kafka topic configuration not found in properties. Expected topic name: " +
                expectedName)
            );
    }
}
