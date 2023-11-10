package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiError {

    HttpStatus status;
    String message;
    List<String> reason;
    String timestamp;
}