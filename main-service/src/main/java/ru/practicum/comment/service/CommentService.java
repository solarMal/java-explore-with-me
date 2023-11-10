package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.dto.CommentShortResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(long userId, long eventId, CommentRequestDto commentRequestDto);

    CommentShortResponseDto updateComment(long userId, long commentId, CommentRequestDto commentRequestDto);

    CommentResponseDto getComment(long userId, long commentId);

    List<CommentShortResponseDto> getComments(long userId);

    void deleteComment(long userId, long commentId);
}