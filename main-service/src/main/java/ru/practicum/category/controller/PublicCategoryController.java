package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET: Receiving categories with pagination");
        return categoryService.getCategories(from, size);
    }

    @GetMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable long catId) {
        log.info("GET: Receiving category with ID: '{}'", catId);
        return categoryService.getCategory(catId);
    }
}