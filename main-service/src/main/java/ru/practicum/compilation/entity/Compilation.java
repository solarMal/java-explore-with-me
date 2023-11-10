package ru.practicum.compilation.entity;

import lombok.*;
import ru.practicum.event.entity.Event;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @Column(name = "compilation_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "compilation_pinned", nullable = false)
    private boolean pinned;

    @Column(name = "compilation_title", nullable = false, length = 50)
    private String title;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;
}