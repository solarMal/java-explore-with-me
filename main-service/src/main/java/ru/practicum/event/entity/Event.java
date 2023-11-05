package ru.practicum.event.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.category.entity.Category;
import ru.practicum.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @Column(name = "event_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "event_title", length = 120, nullable = false)
    private String title;

    @Column(name = "event_annotation", length = 2000, nullable = false)
    private String annotation;

    @Column(name = "event_description", length = 7000, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_initiator_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_location_id", nullable = false)
    private Location location;

    @Column(name = "event_event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "event_created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_published_on")
    private LocalDateTime publishedOn;

    @Column(name = "event_participant_limit", nullable = false)
    private int participantLimit;

    @Column(name = "event_paid", nullable = false)
    private boolean paid;

    @Column(name = "event_request_moderation", nullable = false)
    private boolean requestModeration;

    @Column(name = "event_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;
}