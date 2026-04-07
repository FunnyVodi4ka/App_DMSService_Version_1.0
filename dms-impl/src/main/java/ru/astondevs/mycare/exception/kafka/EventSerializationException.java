package ru.astondevs.mycare.exception.kafka;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsError;
import ru.astondevs.mycare.event.DomainEvent;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.HasHttpStatus;

/**
 * Системное Runtime-исключение, выбрасываемое при критической
 * ошибке сериализации {@link DomainEvent} в JSON.
 * <p>
 * Возникновение этого исключения сигнализирует об ошибке
 * конфигурации (например, циклическая ссылка в DTO)
 * и должно приводить к откату транзакции.
 * <p>
 * Реализует {@link LoggableAsError} — маркерный интерфейс
 * для вашего {@code starterexceptionhandler}, который сигнализирует:
 * <ol>
 * <li><b>Уровень лога:</b> Это неожиданная системная ошибка (Server Error),
 * ее следует логировать на уровне <b>ERROR</b>.</li>
 * <li><b>HTTP-статус:</b> Так как {@link LoggableAsError}
 * наследует {@link HasHttpStatus}, этот класс <b>обязан</b> реализовывать
 * метод {@link #getStatus()}.</li>
 * </ol>
 *
 * @author Ivan Sergienko
 * @version 1.0
 */
public class EventSerializationException extends RuntimeException
    implements LoggableAsError {

    public EventSerializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализует обязательный метод из интерфейса {@link HasHttpStatus}.
     *
     * @return {@link HttpStatus#INTERNAL_SERVER_ERROR} (500).
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
