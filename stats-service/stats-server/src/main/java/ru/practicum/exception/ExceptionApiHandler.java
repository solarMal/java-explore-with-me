package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        log.error("Status cod: 400 --> {}", e.getMessage());
        ApiError apiError = generateApiError(e);
        apiError.setStatus(HttpStatus.BAD_REQUEST);
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