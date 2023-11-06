package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventResponseDto createEvent(long userId, NewEventRequestDto newEventRequestDto);

    EventResponseDto updateEvent(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto);

    List<EventShortResponseDto> getEvents(long userId, int from, int size);

    EventResponseDto getEvent(long userId, long eventId);

    List<EventResponseDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, int from, int size);

    EventResponseDto updateEventByAdmin(long eventId, UpdateEventRequestDto updateEventRequestDto);

    EventRequestStatusUpdateResponse updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<RequestDto> getEventRequests(long userId, long eventId);

    EventResponseDto getEventByPublic(long id, HttpServletRequest request);

    List<EventShortResponseDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);
}