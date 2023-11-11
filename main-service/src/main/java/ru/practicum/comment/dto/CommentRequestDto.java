package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class CommentRequestDto {

    private long id;

    @Positive
    private Long replyTo;

    @NotBlank
    @Size(min = 5, max = 400)
    private String text;
}