package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationResponseDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto getCompilation(@PathVariable long compId) {
        log.info("GET: Receiving compilation with ID: '{}'", compId);
        return compilationService.getCompilation(compId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponseDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET: Trying to receive compilations with parameters");
        return compilationService.getCompilations(pinned, from, size);
    }
}