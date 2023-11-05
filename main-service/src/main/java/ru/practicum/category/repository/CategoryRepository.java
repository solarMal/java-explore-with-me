package ru.practicum.category.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @NonNull Page<Category> findAll(@NonNull Pageable pageable);
}