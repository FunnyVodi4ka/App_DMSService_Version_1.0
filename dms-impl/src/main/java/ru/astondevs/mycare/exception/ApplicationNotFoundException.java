package ru.astondevs.mycare.exception;

import static ru.astondevs.mycare.util.ExceptionMessage.INSURANCE_APPLICATION_NOT_FOUND;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ApplicationNotFoundException extends RuntimeException implements LoggableAsWarning {

    public ApplicationNotFoundException(String id) {
        super(INSURANCE_APPLICATION_NOT_FOUND.formatted(id));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}