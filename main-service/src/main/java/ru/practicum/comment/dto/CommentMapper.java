package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import static ru.practicum.user.dto.UserMapper.toUserShortDto;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(CommentRequestDto commentRequestDto, User author, Event event) {
        return Comment.builder()
                .author(author)
                .event(event)
                .text(commentRequestDto.getText())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .createdOn(comment.getCreatedOn())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .author(toUserShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .replyTo((comment.getReplyTo()) != null ? comment.getReplyTo().getId() : null)
                .text(comment.getText())
                .build();
    }

    public static CommentShortResponseDto toCommentShortResponseDto(Comment comment) {
        return CommentShortResponseDto.builder()
                .id(comment.getId())
                .author(toUserShortDto(comment.getAuthor()))
                .replyTo((comment.getReplyTo()) != null ? comment.getReplyTo().getId() : null)
                .text(comment.getText())
                .build();
    }
}