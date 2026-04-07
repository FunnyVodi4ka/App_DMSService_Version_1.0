package ru.astondevs.mycare.exception.insuranceapplication;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;
import ru.astondevs.mycare.models.entity.InsuranceApplication;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.HasHttpStatus;

/**
 * Бизнес-исключение (Runtime), выбрасываемое, когда {@link InsuranceApplication}
 * не может быть найдено в базе данных по-заданному идентификатору.
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
 * @author Ivan Sergienko
 * @version 1.0
 */
public class InsuranceApplicationNotFoundException extends RuntimeException
    implements LoggableAsWarning {

    public InsuranceApplicationNotFoundException(String message) {
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
