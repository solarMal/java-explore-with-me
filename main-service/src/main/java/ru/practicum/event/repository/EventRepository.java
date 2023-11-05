package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.EventShortResponseDto;
import ru.practicum.event.entity.Event;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("SELECT new ru.practicum.dto.EventShortResponseDto(e.id, e.title, e.description, e.startDate, e.endDate, e.views, COUNT(cr)) " +
            "FROM Event e " +
            "LEFT JOIN e.confirmedRequests cr " +
            "WHERE e.initiatorId = :userId " +
            "GROUP BY e.id")
    Page<EventShortResponseDto> findEventsWithViewsAndConfirmedRequestsByUserId(@Param("userId") long userId, Pageable pageable);

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(long userId, long eventId);

    Set<Event> findByIdIn(Set<Long> eventIds);
}