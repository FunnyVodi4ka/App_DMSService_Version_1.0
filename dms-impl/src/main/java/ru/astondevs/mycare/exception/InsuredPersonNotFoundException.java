package ru.astondevs.mycare.exception;

import static ru.astondevs.mycare.util.ExceptionMessage.INSURED_PERSON_NOT_FOUND;

import java.io.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InsuredPersonNotFoundException extends RuntimeException implements LoggableAsWarning {

    public InsuredPersonNotFoundException(String id) {
        super(INSURED_PERSON_NOT_FOUND.formatted(id));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
