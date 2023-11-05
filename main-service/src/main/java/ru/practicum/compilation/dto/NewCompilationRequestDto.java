package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class NewCompilationRequestDto {

    private long id;
    private boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "Field: title. Error: size must be >= 1 and <= 50 characters")
    private String title;
    private Set<Long> events;
}