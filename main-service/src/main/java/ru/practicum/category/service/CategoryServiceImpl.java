package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.category.dto.CategoryMapper.toCategory;
import static ru.practicum.category.dto.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(toCategory(categoryDto));
        log.info("Category with ID: '" + category.getId() + "' successfully created");
        return toCategoryDto(category);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID: '" + catId + "' not found"));
        categoryRepository.delete(category);
        log.info("Category with ID: '{}' successfully removed", category.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<CategoryDto> result = categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("{} categories found by request", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID: '" + catId + "' not found"));
        log.info("Category with ID: '{}' successfully received", category.getId());
        return toCategoryDto(category);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public CategoryDto updateCategory(CategoryDto categoryDto, long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID: '" + catId + "' not found"));
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.info("Category with ID: '{}' successfully updated", category.getId());
        return toCategoryDto(category);
    }
}