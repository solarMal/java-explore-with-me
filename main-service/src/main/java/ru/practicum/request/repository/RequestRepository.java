package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.dto.EventRequestsCount;
import ru.practicum.request.entity.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(long userId, long eventId);

    boolean existsByIdAndRequesterId(long requestId, long userId);

    List<Request> findAllByRequesterId(long userId);

    @Query("select new ru.practicum.request.dto.EventRequestsCount(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id = :eventId and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    EventRequestsCount getAmountOfConfirmedRequests(long eventId);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findByEventId(long eventId);

    @Query("select new ru.practicum.request.dto.EventRequestsCount(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id in :eventIds and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<EventRequestsCount> getAmountOfConfirmedRequestsOfEvents(List<Long> eventIds);
}