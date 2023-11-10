package ru.practicum.category.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.entity.Category;

@UtilityClass
public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}