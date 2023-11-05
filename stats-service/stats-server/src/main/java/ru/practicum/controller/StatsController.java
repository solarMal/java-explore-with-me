package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsResponseDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody StatsRequestDto statsRequestDto) {
        log.info("POST: Saving hit for {}", statsRequestDto.getApp());
        statsService.saveHit(statsRequestDto);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<List<StatsResponseDto>> getStats(@RequestParam String start,
                                                           @RequestParam String end,
                                                           @RequestParam(required = false) List<String> uris,
                                                           @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET: Receiving stats from {} to {}", start, end);
        try {
            LocalDateTime starting = LocalDateTime.parse(start, FORMATTER);
            LocalDateTime ending = LocalDateTime.parse(end, FORMATTER);
            if (starting.isAfter(ending)) {
                throw new ValidationException("Parameter rangeStart should be before rangeEnd");
            }
            return new ResponseEntity<>(statsService.getStats(starting, ending, uris, unique), HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}