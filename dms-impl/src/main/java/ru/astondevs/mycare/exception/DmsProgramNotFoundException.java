package ru.astondevs.mycare.exception;

import static ru.astondevs.mycare.util.ExceptionMessage.DMS_PROGRAM_NOT_FOUND;

import org.springframework.http.HttpStatus;
import ru.astondevs.mycare.starterexceptionhandler.exception.marker.LoggableAsWarning;

public class DmsProgramNotFoundException extends RuntimeException implements LoggableAsWarning {

    public DmsProgramNotFoundException(String id) {
        super(DMS_PROGRAM_NOT_FOUND.formatted(id));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
