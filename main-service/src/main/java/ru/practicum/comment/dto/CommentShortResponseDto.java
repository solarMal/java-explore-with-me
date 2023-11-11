package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.user.dto.UserShortDto;

@Data
@Builder
@AllArgsConstructor
public class CommentShortResponseDto {

    private long id;
    private UserShortDto author;
    private Long replyTo;
    private String text;
}