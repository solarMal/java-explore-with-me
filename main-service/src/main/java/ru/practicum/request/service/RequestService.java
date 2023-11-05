package ru.practicum.request.service;

import ru.practicum.event.entity.Event;
import ru.practicum.request.dto.RequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    RequestDto createRequest(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getOwnRequests(long userId);

    Map<Long, Long> getConfirmedRequests(List<Event> events);
}