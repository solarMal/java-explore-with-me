package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationResponseDto;
import ru.practicum.compilation.dto.NewCompilationRequestDto;
import ru.practicum.compilation.dto.UpdateCompilationRequestDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@Valid @RequestBody NewCompilationRequestDto compilationRequestDto) {
        log.info("POST: Creating compilation from request body: {}", compilationRequestDto);
        return compilationService.createCompilation(compilationRequestDto);
    }

    @PatchMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto updateCompilation(@PathVariable long compId,
                                                    @Valid @RequestBody UpdateCompilationRequestDto compilationRequestDto) {
        log.info("PATCH: Trying to update compilation with ID: '{}' from request body: {}", compId, compilationRequestDto);
        return compilationService.updateCompilation(compId, compilationRequestDto);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("DELETE: Trying to delete compilation with ID: '{}'", compId);
        compilationService.deleteCompilation(compId);
    }
}