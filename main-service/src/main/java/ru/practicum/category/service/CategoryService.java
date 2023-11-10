package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);

    CategoryDto updateCategory(CategoryDto categoryDto, long catId);
}