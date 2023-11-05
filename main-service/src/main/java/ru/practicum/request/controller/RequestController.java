package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequest(@PathVariable long userId,
                             @RequestParam(name = "eventId") @Positive long eventId) {
        log.info("POST: Creating request to event with ID:'{}' from user with ID: '{}'", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping(path = "/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    RequestDto cancelRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("PATCH: User with ID: '{}' trying to cancel his request with ID: '{}'", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<RequestDto> getOwnRequests(@PathVariable long userId) {
        log.info("GET: User with ID: '{}' trying to receive his own requests", userId);
        return requestService.getOwnRequests(userId);
    }
}