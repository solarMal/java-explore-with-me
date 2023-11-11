package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByReplyToId(long replyToId);

    List<Comment> findByAuthorId(long userId);

    List<Comment> findByEventId(long id);

    List<Comment> findByEventIn(Collection<Event> events);
}