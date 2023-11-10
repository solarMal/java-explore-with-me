package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@PathVariable long userId,
                                        @Valid @RequestBody NewEventRequestDto newEventRequestDto) {
        log.info("POST: Creating event from user with ID: '{}' with request body: {}", userId, newEventRequestDto);
        return eventService.createEvent(userId, newEventRequestDto);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {
        log.info("PATCH: Trying to update event with ID: '{}' by user with ID: '{}'", eventId, userId);
        return eventService.updateEvent(userId, eventId, updateEventRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortResponseDto> getEvents(@PathVariable long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET: Trying to receive events of user with ID: '{}' with pagination", userId);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("GET: Trying to receive event with ID: '{}' by user with ID: '{}'", eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResponse updateEventRequests(@PathVariable long userId,
                                                                @PathVariable long eventId,
                                                                @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("PATCH: Trying to update statuses of requests of event with ID: '{}'", eventId);
        return eventService.updateEventRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getEventRequest(@PathVariable long userId, @PathVariable long eventId) {
        log.info("GET: Trying to receive requests to event with ID: '{}'", eventId);
        return eventService.getEventRequests(userId, eventId);
    }
}