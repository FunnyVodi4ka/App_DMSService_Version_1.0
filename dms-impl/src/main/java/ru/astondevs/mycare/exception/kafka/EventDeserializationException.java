package ru.astondevs.mycare.exception.kafka;


import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsError;

/**
 * Исключение, возникающее при ошибке десериализации события.
 * <p>
 * Выбрасывается, когда входящее сообщение (например, из Kafka) не удается преобразовать
 * в целевой объект (DTO/Event). Обычно это указывает на нарушение контракта данных
 * или повреждение сообщения.
 * </p>
 * <p>
 * Класс реализует интерфейс {@link LoggableAsError}, что инструктирует глобальный
 * обработчик ошибок логировать это исключение с уровнем ERROR.
 * </p>
 *
 * @author Ivan Sakharov
 * @since 11/16/2025
 */

public class EventDeserializationException  extends RuntimeException
    implements LoggableAsError {

    /**
     * Конструктор исключения.
     *
     * @param message   Сообщение, описывающее детали ошибки.
     * @param throwable Причина (исходное исключение), вызвавшая ошибку десериализации (например, JsonParseException).
     */
    public EventDeserializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * @return {@link HttpStatus#INTERNAL_SERVER_ERROR},
     * так как это внутренняя, неожиданная ошибка обработки данных.
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}