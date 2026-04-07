package ru.astondevs.mycare.exception.kafka;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsError;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.HasHttpStatus;

/**
 * Исключение, выбрасываемое, когда обработчик Kafka
 * (например, {@code OutboxEventAcknowledger})
 * получает сообщение, в котором отсутствует
 * обязательный заголовок 'outbox_id'.
 * <p>
 * Это критическая ошибка контракта, которая
 * приводит к откату транзакции
 * и инициирует механизм Retry/DLT.
 * <p>
 * Реализует {@link LoggableAsError}, что
 * сигнализирует {@code KafkaErrorProcessor}
 * о необходимости логирования на уровне ERROR.
 *
 * @author Ivan Sergienko
 * @since 1.0.0
 */
public class MissingOutboxIdException extends RuntimeException
    implements LoggableAsError {

    public MissingOutboxIdException(String message) {
        super(message);
    }

    /**
     * Реализует обязательный метод из
     * интерфейса {@link HasHttpStatus}
     * (который наследуется от {@link LoggableAsError}).
     *
     * @return {@link HttpStatus#INTERNAL_SERVER_ERROR} (500),
     * так как это непредвиденная ошибка
     * обработки на стороне сервера (консьюмера).
     */
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
