package ru.astondevs.mycare.exception.kafka;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsError;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.HasHttpStatus;

/**
 * Критическое Runtime-исключение, выбрасываемое при запуске приложения,
 * если необходимая конфигурация топика Kafka не найдена
 * в {@code starter.kafka.topics}.
 * <p>
 * Это фатальная ошибка конфигурации, которая должна прерывать запуск
 * приложения.
 * <p>
 * Реализует {@link LoggableAsError} — маркерный интерфейс
 * для вашего {@code starterexceptionhandler}, который сигнализирует:
 * <ol>
 * <li><b>Уровень лога:</b> Это критическая ошибка конфигурации (Server Error),
 * ее следует логировать на уровне <b>ERROR</b>.</li>
 * <li><b>HTTP-статус:</b> Так как {@link LoggableAsError}
 * наследует {@link HasHttpStatus}, этот класс <b>обязан</b> реализовывать
 * метод {@link #getStatus()}.</li>
 * </ol>
 *
 * @author Ivan Sergienko (предположительно)
 * @version 1.0
 */
public class TopicConfigurationNotFoundException extends RuntimeException
    implements LoggableAsError {

    public TopicConfigurationNotFoundException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализует обязательный метод из интерфейса {@link HasHttpStatus}.
     *
     * @return {@link HttpStatus#INTERNAL_SERVER_ERROR} (500), так как
     * это ошибка инициализации сервера.
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
