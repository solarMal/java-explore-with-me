package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.event.dto.EventShortResponseDto;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class CompilationResponseDto {

    private long id;
    private boolean pinned;
    private String title;
    private Set<EventShortResponseDto> events;
}