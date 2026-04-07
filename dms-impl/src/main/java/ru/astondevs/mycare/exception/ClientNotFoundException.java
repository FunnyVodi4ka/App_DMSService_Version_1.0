package ru.astondevs.mycare.exception;

import static ru.astondevs.mycare.util.ExceptionMessage.CLIENT_NOT_FOUND;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;

public class ClientNotFoundException extends RuntimeException implements LoggableAsWarning {

    public ClientNotFoundException(String id) {
        super(CLIENT_NOT_FOUND.formatted(id));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
