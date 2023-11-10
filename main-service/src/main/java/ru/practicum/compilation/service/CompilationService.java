package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationResponseDto;
import ru.practicum.compilation.dto.NewCompilationRequestDto;
import ru.practicum.compilation.dto.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto createCompilation(NewCompilationRequestDto compilationRequestDto);

    CompilationResponseDto updateCompilation(long compId, UpdateCompilationRequestDto compilationRequestDto);

    CompilationResponseDto getCompilation(long compId);

    List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    void deleteCompilation(long compId);
}