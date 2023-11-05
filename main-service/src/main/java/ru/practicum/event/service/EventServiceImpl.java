package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.entity.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PermissionException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestsCount;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.entity.Request;
import ru.practicum.request.entity.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.RequestService;
import ru.practicum.stat.StatService;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.dto.EventMapper.*;
import static ru.practicum.request.dto.RequestMapper.toRequestDto;
import static ru.practicum.request.entity.RequestStatus.parseRequestStatus;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final RequestService requestService;
    private final StatService statService;

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, ValidationException.class})
    public EventResponseDto createEvent(long userId, NewEventRequestDto newEventRequestDto) {
        LocalDateTime eventDate = checkEventDate(newEventRequestDto.getEventDate());
        User initiator = findUser(userId);
        Category category = findCategory(newEventRequestDto.getCategory());
        Location location = checkLocation(newEventRequestDto.getLocation());
        Event event = toEvent(newEventRequestDto, eventDate, initiator, category, location);
        eventRepository.save(event);
        log.info("Event with ID: '{}' successfully created", event.getId());
        return toEventResponseDto(event);
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, NotAvailableException.class, PermissionException.class})
    public EventResponseDto updateEvent(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        checkInitiator(event, initiator);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new PermissionException("User couldn't update already published event");
        }
        Optional<StateAction> oState = checkStateAction(updateEventRequestDto.getStateAction());
        if (oState.isPresent()) {
            if (oState.get().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
            if (oState.get().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        updateOtherFieldsFromRequest(event, updateEventRequestDto);
        event = eventRepository.save(event);
        EventResponseDto responseDto = updateViewsAndConfirmedRequestsInEventResponseDto(event);
        log.info("Event with ID:'{}' successfully updated", event.getId());
        return responseDto;
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getEvents(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        List<EventShortResponseDto> result = updateViewsAndConfirmedRequestsInEventShortResponseDto(events.toList());
        log.info("{} events found by request", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEvent(long userId, long eventId) {
        findUser(userId);
        Optional<Event> event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with ID: '" + eventId + "' not found");
        } else {
            EventResponseDto responseDto = updateViewsAndConfirmedRequestsInEventResponseDto(event.get());
            log.info("Event with ID: '{}' of user with ID: '{}' found", eventId, userId);
            return responseDto;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getEventsByAdmin(List<Long> users,
                                                   List<String> states,
                                                   List<Long> categories,
                                                   String rangeStart,
                                                   String rangeEnd,
                                                   int from,
                                                   int size) {
        checkStartEndRange(rangeStart, rangeEnd);
        BooleanExpression filter = makeAdminFilterByParameters(users, states, categories, rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Iterable<Event> iterable = eventRepository.findAll(filter, pageable);
        List<EventResponseDto> result = new ArrayList<>();
        EventResponseDto responseDto;
        for (Event event : iterable) {
            responseDto = updateViewsAndConfirmedRequestsInEventResponseDto(event);
            result.add(responseDto);
        }
        log.info("{} events found by request", result.size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, ValidationException.class, PermissionException.class})
    public EventResponseDto updateEventByAdmin(long eventId, UpdateEventRequestDto updateEventRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        Event event = findEvent(eventId);
        Optional<StateAction> oState = checkStateAction(updateEventRequestDto.getStateAction());
        if (oState.isPresent()) {
            StateAction state = oState.get();
            switch (state) {
                case PUBLISH_EVENT:
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new PermissionException("Only pending events can be published");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(now);
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        throw new PermissionException("Only not published events can be rejected");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventRequestDto.getEventDate() != null) {
            LocalDateTime eventDayFromRequest = LocalDateTime.parse(
                    updateEventRequestDto.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (eventDayFromRequest.isBefore(now.plusHours(1))) {
                throw new ValidationException("Event date is incorrect. Should be not earlier than 1 hour from publication");
            }
        }
        updateOtherFieldsFromRequest(event, updateEventRequestDto);
        event = eventRepository.save(event);
        EventResponseDto responseDto = updateViewsAndConfirmedRequestsInEventResponseDto(event);
        log.info("Event with ID:'{}' successfully updated", event.getId());
        return responseDto;
    }

    @Override
    @Transactional(rollbackFor = {NotFoundException.class, ValidationException.class, PermissionException.class})
    public EventRequestStatusUpdateResponse updateEventRequests(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        checkInitiator(event, initiator);
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new PermissionException("Confirmation of requests is not required");
        }
        List<Request> requests = checkRequestIds(eventRequestStatusUpdateRequest);
        EventRequestsCount amountOfConfirmedRequests = requestRepository.getAmountOfConfirmedRequests(eventId);
        long count = (amountOfConfirmedRequests == null) ? 0 : amountOfConfirmedRequests.getCount();
        if (count == event.getParticipantLimit()) {
            throw new PermissionException("Participant limit of event already reached");
        }
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        for (Request request : requests) {
            if (count < event.getParticipantLimit()) {
                RequestStatus requestStatus = parseRequestStatus(eventRequestStatusUpdateRequest.getStatus());
                if (requestStatus.equals(RequestStatus.CONFIRMED)) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    confirmedRequests.add(toRequestDto(request));
                }
                if (requestStatus.equals(RequestStatus.REJECTED)) {
                    request.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(toRequestDto(request));
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(toRequestDto(request));
            }
        }
        log.info("Status of events with ID:'{}' successfully updated", eventRequestStatusUpdateRequest.getRequestIds());
        return EventRequestStatusUpdateResponse.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getEventRequests(long userId, long eventId) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        checkInitiator(event, initiator);
        List<Request> requests = requestRepository.findByEventId(eventId);
        log.info("{} requests found by request", requests.size());
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventByPublic(long id, HttpServletRequest request) {
        Event event = findEvent(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with ID: '" + id + "' not published");
        }
        EventResponseDto responseDto = updateViewsAndConfirmedRequestsInEventResponseDto(event);
        log.info("Event with ID: '{}' found", id);
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getEventsByPublic(String text,
                                                         List<Long> categories,
                                                         Boolean paid,
                                                         String rangeStart,
                                                         String rangeEnd,
                                                         Boolean onlyAvailable,
                                                         String sort,
                                                         int from,
                                                         int size,
                                                         HttpServletRequest request) {
        checkStartEndRange(rangeStart, rangeEnd);
        BooleanExpression filter = makePublicFilterByParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        Sort sortMethod;
        if (sort != null && sort.equals(EventSort.EVENT_DATE.toString())) {
            sortMethod = Sort.by("eventDate");
        } else {
            sortMethod = Sort.by("id");
        }
        Pageable pageable = PageRequest.of(from / size, size, sortMethod);
        Page<Event> events = eventRepository.findAll(filter, pageable);
        List<EventShortResponseDto> result = updateViewsAndConfirmedRequestsInEventShortResponseDto(events.toList());
        if (sort != null && sort.equals(EventSort.VIEWS.toString())) {
            result.sort(Comparator.comparing(EventShortResponseDto::getViews));
        }
        log.info("{} events found by request", result.size());
        return result;
    }

    private List<Request> checkRequestIds(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<Long> ids = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> checkedRequests = requestRepository.findAllByIdIn(ids);
        for (Request request : checkedRequests) {
            if (!ids.contains(request.getId())) {
                throw new NotFoundException("Request with ID:'" + request.getId() + "doesn't exist");
            }
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new PermissionException("Status of request with ID: '" + request.getId() + "' is invalid. Should be 'PENDING'");
            }
        }
        return checkedRequests;
    }

    private User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID: '" + id + "' not found"));
    }

    private Category findCategory(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID: '" + catId + "' not found"));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID: '" + eventId + "' not found"));
    }

    private Location checkLocation(Location location) {
        Location result = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (result == null) {
            result = locationRepository.save(location);
        }
        return result;
    }

    private LocalDateTime checkEventDate(String eventDate) {
        LocalDateTime result = LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (result.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date is incorrect. Should be not earlier than 2 hours");
        }
        return result;
    }

    private void checkInitiator(Event event, User initiator) {
        if (event.getInitiator().getId() != initiator.getId()) {
            throw new NotFoundException("User with ID: '" + initiator.getId()
                    + "couldn't update event with ID: '" + event.getId() + "'");
        }
    }

    private Optional<StateAction> checkStateAction(String state) {
        Optional<StateAction> result = Optional.empty();
        for (StateAction stateAction : StateAction.values()) {
            if (stateAction.name().equalsIgnoreCase(state)) {
                result = Optional.of(stateAction);
            }
        }
        return result;
    }

    private void updateOtherFieldsFromRequest(Event event, UpdateEventRequestDto updateEventRequestDto) {
        Optional.ofNullable(updateEventRequestDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventRequestDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventRequestDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventRequestDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventRequestDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventRequestDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        if (updateEventRequestDto.getCategory() != null) {
            event.setCategory(findCategory(updateEventRequestDto.getCategory()));
        }
        if (updateEventRequestDto.getLocation() != null) {
            event.setLocation(checkLocation(updateEventRequestDto.getLocation()));
        }
        if (updateEventRequestDto.getEventDate() != null) {
            event.setEventDate(checkEventDate(updateEventRequestDto.getEventDate()));
        }
    }

    private void checkStartEndRange(String rangeStart, String rangeEnd) {
        LocalDateTime start = (rangeStart == null) ? null : LocalDateTime.parse(rangeStart, FORMATTER);
        LocalDateTime end = (rangeEnd == null) ? null : LocalDateTime.parse(rangeEnd, FORMATTER);
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Parameter rangeStart should be before rangeEnd");
        }
    }

    private BooleanExpression makeAdminFilterByParameters(List<Long> users,
                                                          List<String> states,
                                                          List<Long> categories,
                                                          String rangeStart,
                                                          String rangeEnd) {
        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null) {
            BooleanExpression byUsers = QEvent.event.initiator.id.in(users);
            conditions.add(byUsers);
        }
        if (states != null) {
            List<EventState> eventStates = new ArrayList<>();
            for (String state : states) {
                eventStates.add(EventState.parseState(state));
            }
            BooleanExpression byStates = QEvent.event.state.in(eventStates);
            conditions.add(byStates);
        }
        if (categories != null) {
            BooleanExpression byCategories = QEvent.event.category.id.in(categories);
            conditions.add(byCategories);
        }
        conditions = addRangeStartAndRangeEndToFilter(conditions, rangeStart, rangeEnd);
        if (conditions.isEmpty()) {
            return Expressions.TRUE.isTrue();
        } else {
            return conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
        }
    }

    private BooleanExpression makePublicFilterByParameters(String text,
                                                           List<Long> categories,
                                                           Boolean paid,
                                                           String rangeStart,
                                                           String rangeEnd,
                                                           Boolean onlyAvailable) {
        List<BooleanExpression> conditions = new ArrayList<>();
        if (text != null) {
            BooleanExpression byText = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            conditions.add(byText);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression byCategory = QEvent.event.category.id.in(categories);
            conditions.add(byCategory);
        }
        if (paid != null) {
            BooleanExpression byPaid = QEvent.event.paid.eq(paid);
            conditions.add(byPaid);
        }
        conditions = addRangeStartAndRangeEndToFilter(conditions, rangeStart, rangeEnd);
        if (onlyAvailable != null) {
            BooleanExpression byOnlyAvailable = QEvent.event.participantLimit.goe(0);
            conditions.add(byOnlyAvailable);
        }
        BooleanExpression byPublished = QEvent.event.state.eq(EventState.PUBLISHED);
        conditions.add(byPublished);
        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private List<BooleanExpression> addRangeStartAndRangeEndToFilter(List<BooleanExpression> conditions, String rangeStart, String rangeEnd) {
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, FORMATTER);
            BooleanExpression byRangeStart = QEvent.event.eventDate.after(start);
            conditions.add(byRangeStart);
        }
        if (rangeEnd != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, FORMATTER);
            BooleanExpression byRangeEnd = QEvent.event.eventDate.before(end);
            conditions.add(byRangeEnd);
        }
        return conditions;
    }

    private EventResponseDto updateViewsAndConfirmedRequestsInEventResponseDto(Event event) {
        Map<Long, Long> views = statService.getStats(List.of(event));
        Map<Long, Long> requests = requestService.getConfirmedRequests(List.of(event));
        EventResponseDto responseDto = toEventResponseDto(event);
        responseDto.setViews(views.getOrDefault(event.getId(), 0L));
        responseDto.setConfirmedRequests(requests.getOrDefault(event.getId(), 0L));
        return responseDto;
    }

    private List<EventShortResponseDto> updateViewsAndConfirmedRequestsInEventShortResponseDto(List<Event> events) {
        List<EventShortResponseDto> result = new ArrayList<>();
        Map<Long, Long> views = statService.getStats(events);
        Map<Long, Long> requests = requestService.getConfirmedRequests(events);
        EventShortResponseDto responseDto;
        for (Event event : events) {
            responseDto = toEventShortResponseDto(event);
            responseDto.setViews(views.getOrDefault(event.getId(), 0L));
            responseDto.setConfirmedRequests(requests.getOrDefault(event.getId(), 0L));
            result.add(responseDto);
        }
        return result;
    }
}