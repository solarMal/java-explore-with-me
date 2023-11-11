package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.dto.CommentShortResponseDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PermissionException;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.comment.dto.CommentMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(long userId, long eventId, CommentRequestDto commentRequestDto) {
        Comment replyTo = null;
        Long replyToId = commentRequestDto.getReplyTo();
        if (replyToId != null) {
            replyTo = findComment(replyToId);
        }
        Event event = findEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotAvailableException("Couldn't create comment to not published event");
        }
        User author = findUser(userId);
        Comment comment = toComment(commentRequestDto, author, event);
        comment.setReplyTo(replyTo);
        commentRepository.save(comment);
        log.info("Comment with ID: '" + comment.getId() + "' successfully created");
        return toCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public CommentShortResponseDto updateComment(long userId, long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = checkComment(userId, commentId);
        if (!commentRepository.findByReplyToId(commentId).isEmpty()) {
            throw new PermissionException("Couldn't update comment witch already has any replies");
        }
        if (commentRequestDto.getReplyTo() != null) {
            throw new PermissionException("Only text of comment could be updated");
        }
        Optional.ofNullable(commentRequestDto.getText()).ifPresent(comment::setText);
        log.info("Comment with ID: '" + comment.getId() + "' successfully updated");
        return toCommentShortResponseDto(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto getComment(long userId, long commentId) {
        Comment comment = checkComment(userId, commentId);
        log.info("Comment with ID: '" + comment.getId() + "' successfully received");
        return toCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public List<CommentShortResponseDto> getComments(long userId) {
        findUser(userId);
        List<CommentShortResponseDto> result = commentRepository.findByAuthorId(userId).stream()
                .map(CommentMapper::toCommentShortResponseDto)
                .collect(Collectors.toList());
        log.info("{} comments found by request", result.size());
        return result;
    }

    @Override
    @Transactional
    public void deleteComment(long userId, long commentId) {
        Comment comment = checkComment(userId, commentId);
        if (!commentRepository.findByReplyToId(commentId).isEmpty()) {
            throw new PermissionException("Couldn't delete comment witch already has any replies");
        }
        commentRepository.delete(comment);
        log.info("Comment with ID: '{}' successfully removed", comment.getId());
    }

    private Comment checkComment(long userId, long commentId) {
        User author = findUser(userId);
        Comment comment = findComment(commentId);
        if (author.getId() != comment.getAuthor().getId()) {
            throw new PermissionException("Only author can get comment");
        }
        return comment;
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: '" + userId + "' not found"));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID: '" + eventId + "' not found"));
    }

    private Comment findComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with ID: '" + commentId + "' not found"));
    }
}