package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.dto.CommentShortResponseDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/user/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable long userId,
                                            @PathVariable long eventId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Trying to create comment to event with ID: '{}' from user with ID: '{}'", eventId, userId);
        return commentService.createComment(userId, eventId, commentRequestDto);
    }

    @PatchMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentShortResponseDto updateComment(@PathVariable long userId,
                                                 @PathVariable long commentId,
                                                 @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Trying to update comment with ID: '{}' by user with ID: '{}'", commentId, userId);
        return commentService.updateComment(userId, commentId, commentRequestDto);
    }

    @GetMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto getComment(@PathVariable long userId,
                                         @PathVariable long commentId) {
        log.info("Trying to receive comment with ID: '{}' by user with ID: '{}'", commentId, userId);
        return commentService.getComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortResponseDto> getComments(@PathVariable long userId) {
        log.info("Trying to receive comments of user with ID: '{}'", userId);
        return commentService.getComments(userId);
    }

    @DeleteMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        log.info("Trying to delete comment with ID: '{}' by user with ID: '{}'", commentId, userId);
        commentService.deleteComment(userId, commentId);
    }
}