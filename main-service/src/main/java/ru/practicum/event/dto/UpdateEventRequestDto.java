package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.entity.Location;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UpdateEventRequestDto {

    @Size(min = 3, max = 120, message = "Field: title. Error: size must be >= 3 and <= 120 characters")
    private String title;

    @Size(min = 20, max = 2000, message = "Field: annotation. Error: size must be >= 20 and <= 2000 characters")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Field: description. Error: size must be >= 20 and <= 7000 characters")
    private String description;

    @Positive
    private Long category;

    private Location location;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private String stateAction;
}