package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventResponseDto;
import ru.practicum.event.dto.UpdateEventRequestDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDto> getEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                   @RequestParam(name = "states", required = false) List<String> states,
                                                   @RequestParam(name = "categories", required = false) List<Long> categories,
                                                   @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                   @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                   @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET: Trying to receive events by admin with parameters");
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEventByAdmin(@PathVariable long eventId,
                                               @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {
        log.info("PATCH: Trying to update event with ID:'{}' by admin", eventId);
        return eventService.updateEventByAdmin(eventId, updateEventRequestDto);
    }
}