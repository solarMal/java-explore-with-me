package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.compilation.dto.CompilationResponseDto;
import ru.practicum.compilation.dto.NewCompilationRequestDto;
import ru.practicum.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortResponseDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.compilation.dto.CompilationMapper.toCompilation;
import static ru.practicum.compilation.dto.CompilationMapper.toCompilationResponseDto;
import static ru.practicum.event.dto.EventMapper.toEventShortResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CompilationResponseDto createCompilation(NewCompilationRequestDto compilationRequestDto) {
        Set<Long> ids = compilationRequestDto.getEvents();
        Set<Event> events = (ids == null) ? Collections.emptySet() : eventRepository.findByIdIn(ids);
        Set<EventShortResponseDto> eventsDto = updateCommentsForEvents(events);
        Compilation compilation = toCompilation(compilationRequestDto, events);
        compilationRepository.save(compilation);
        log.info("Compilation with ID: '{}' successfully created", compilation.getId());
        return toCompilationResponseDto(compilation, eventsDto);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public CompilationResponseDto updateCompilation(long compId, UpdateCompilationRequestDto compilationRequestDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with ID: '" + compId + "' not found"));
        Optional.ofNullable(compilationRequestDto.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(compilationRequestDto.getTitle()).ifPresent(compilation::setTitle);
        Set<Long> ids = compilationRequestDto.getEvents();
        if (ids != null) {
            compilation.setEvents(eventRepository.findByIdIn(ids));
        }
        compilationRepository.save(compilation);
        log.info("Compilation with ID: '{}' successfully updated", compilation.getId());
        Set<EventShortResponseDto> eventsDto = updateCommentsForEvents(compilation.getEvents());
        return toCompilationResponseDto(compilation, eventsDto);
    }

    @Override
    public CompilationResponseDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with ID: '" + compId + "' not found"));
        Set<EventShortResponseDto> eventsDto = updateCommentsForEvents(compilation.getEvents());
        log.info("Compilation with ID: '{}' successfully received", compilation.getId());
        return toCompilationResponseDto(compilation, eventsDto);
    }

    @Override
    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("pinned"));
        Page<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable);
        }
        Set<EventShortResponseDto> eventsDto;
        List<CompilationResponseDto> result = new ArrayList<>();
        for (Compilation c : compilations) {
            eventsDto = updateCommentsForEvents(c.getEvents());
            result.add(toCompilationResponseDto(c, eventsDto));
        }
        log.info("{} compilations found by request", result.size());
        return result;
    }

    @Override
    public void deleteCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with ID: '" + compId + "' not found"));
        compilationRepository.delete(compilation);
        log.info("Compilation with ID: '{}' successfully removed", compilation.getId());
    }

    private Set<EventShortResponseDto> updateCommentsForEvents(Set<Event> events) {
        List<CommentResponseDto> comments = commentRepository.findByEventIn(events).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        return events.stream()
                .map(e -> toEventShortResponseDto(e,
                        comments.stream()
                                .filter(c -> c.getEventId() == e.getId())
                                .collect(Collectors.toList())))
                .collect(Collectors.toSet());
    }


}