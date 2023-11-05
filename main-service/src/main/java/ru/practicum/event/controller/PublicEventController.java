package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.aop.SaveHitToStats;
import ru.practicum.event.dto.EventResponseDto;
import ru.practicum.event.dto.EventShortResponseDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventController {

    private final EventService eventService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SaveHitToStats
    EventResponseDto getEventByPublic(@PathVariable long id, HttpServletRequest request) {
        log.info("GET: Trying to receive event with ID: '{}'", id);
        log.info("----> client ip: {}", request.getRemoteAddr());
        log.info("----> endpoint path: {}", request.getRequestURI());
        return eventService.getEventByPublic(id, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @SaveHitToStats
    List<EventShortResponseDto> getEventsByPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) String rangeStart,
                                                  @RequestParam(required = false) String rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  HttpServletRequest request) {
        log.info("GET: Trying to receive all events with parameters");
        log.info("----> client ip: {}", request.getRemoteAddr());
        log.info("----> endpoint path: {}", request.getRequestURI());
        return eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }
}