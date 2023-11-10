package ru.practicum.comment.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "comment_created_on", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(name = "comment_last_updated_on")
    @UpdateTimestamp
    private LocalDateTime lastUpdatedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_reply_to_comment_id")
    private Comment replyTo;

    @Column(name = "comment_text", nullable = false)
    private String text;
}