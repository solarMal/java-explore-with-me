package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.dto.EventShortResponseDto;
import ru.practicum.event.entity.Event;

import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationRequestDto compilationRequestDto, Set<Event> events) {
        return Compilation.builder()
                .pinned(compilationRequestDto.isPinned())
                .title(compilationRequestDto.getTitle())
                .events(events)
                .build();
    }

    public static CompilationResponseDto toCompilationResponseDto(Compilation compilation, Set<EventShortResponseDto> events) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(events)
                .build();
    }
}