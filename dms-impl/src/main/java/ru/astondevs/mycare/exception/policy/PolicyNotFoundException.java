package ru.astondevs.mycare.exception.policy;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.models.entity.Policy;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.HasHttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;

/**
 * Бизнес-исключение (Runtime), выбрасываемое, когда {@link Policy}
 * не может быть найден в базе данных по-заданному идентификатору.
 * <p>
 * Реализует {@link LoggableAsWarning} — маркерный интерфейс
 * для вашего {@code starterexceptionhandler}, который сигнализирует:
 * <ol>
 * <li><b>Уровень лога:</b> Это ожидаемая ошибка (Client Error),
 * ее следует логировать на уровне <b>WARN</b>.</li>
 * <li><b>HTTP-статус:</b> Так как {@link LoggableAsWarning} наследует
 * {@link HasHttpStatus}, этот класс <b>обязан</b> реализовывать
 * метод {@link #getStatus()}.</li>
 * </ol>
 *
 * @author Ivan Segen
 * @version 1.0
 */
public class PolicyNotFoundException extends RuntimeException
    implements LoggableAsWarning {

    public PolicyNotFoundException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализует обязательный метод из интерфейса {@link HasHttpStatus}.
     *
     * @return {@link HttpStatus#NOT_FOUND} (404).
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
