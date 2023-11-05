package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class,
            IllegalStateException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        log.error("Status cod: 400 --> {}", e.getMessage());
        ApiError apiError = generateApiError(e);
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        return apiError;
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final Exception e) {
        log.error("Status cod: 404 --> {}", e.getMessage());
        ApiError apiError = generateApiError(e);
        apiError.setStatus(HttpStatus.NOT_FOUND);
        return apiError;
    }

    @ExceptionHandler({PSQLException.class,
            NotAvailableException.class,
            PermissionException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final Exception e) {
        log.error("Status cod: 409 --> {}", e.getMessage());
        ApiError apiError = generateApiError(e);
        apiError.setStatus(HttpStatus.CONFLICT);
        return apiError;
    }

    private ApiError generateApiError(final Exception e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(stackTraseToList(e.getStackTrace()))
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    private List<String> stackTraseToList(StackTraceElement[] st) {
        List<String> result = new ArrayList<>();
        for (StackTraceElement element : st) {
            result.add(element.toString());
        }
        return result;
    }
}