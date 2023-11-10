package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PermissionException;
import ru.practicum.request.dto.EventRequestsCount;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.entity.Request;
import ru.practicum.request.entity.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.request.dto.RequestMapper.toRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, PermissionException.class})
    public RequestDto createRequest(long userId, long eventId) {
        User requester = findUser(userId);
        Event event = findEvent(eventId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new PermissionException("Request from user with ID: '" + userId + "' to event with ID:'" + eventId + "' already exists");
        }
        if (event.getInitiator().getId() == userId) {
            throw new PermissionException("Initiator of event couldn't make request to his own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new PermissionException("Couldn't make request to not published event");
        }
        RequestStatus requestStatus = RequestStatus.PENDING;
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatus.CONFIRMED;
        }
        EventRequestsCount amountOfConfirmedRequests = requestRepository.getAmountOfConfirmedRequests(eventId);
        long count = (amountOfConfirmedRequests == null) ? 0 : amountOfConfirmedRequests.getCount();
        if (event.getParticipantLimit() != 0 && count == event.getParticipantLimit()) {
            throw new PermissionException("Participant limit of event already reached");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(requestStatus)
                .build();
        request = requestRepository.save(request);
        log.info("Request with ID: '{}' successfully created", request.getId());
        return toRequestDto(request);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class})
    public RequestDto cancelRequest(long userId, long requestId) {
        findUser(userId);
        Request request = findRequest(requestId);
        if (requestRepository.existsByIdAndRequesterId(requestId, userId)) {
            request.setStatus(RequestStatus.CANCELED);
        }
        log.info("Request with ID: '{}' successfully canceled", request.getId());
        return toRequestDto(request);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class})
    public List<RequestDto> getOwnRequests(long userId) {
        findUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Requests of user with ID: '{}' successfully received", userId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        Map<Long, Long> result = new HashMap<>();
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<EventRequestsCount> counts = requestRepository.getAmountOfConfirmedRequestsOfEvents(eventIds);
        if (counts.isEmpty()) {
            return result;
        }
        for (EventRequestsCount count : counts) {
            result.put(count.getEventId(), count.getCount());
        }
        return result;
    }

    private Request findRequest(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with ID: '" + requestId + "' not found"));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID:'" + eventId + "' not found"));
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID:'" + userId + "' not found"));
    }
}