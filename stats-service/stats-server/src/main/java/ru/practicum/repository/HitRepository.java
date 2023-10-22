package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsResponseDto;
import ru.practicum.entity.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.StatsResponseDto(a.name, h.uri, count(distinct h.ip)) "
            + "from Hit as h join fetch App as a on a.id = h.app.id "
            + "where (h.timestamp between :start and :end) "
            + "and ((h.uri in :uris) or (coalesce(:uris, '') = '')) "
            + "group by a.name, h.uri "
            + "order by count(distinct h.ip) desc")
    List<StatsResponseDto> findUniqHits(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.StatsResponseDto(a.name, h.uri, count(h.ip)) "
            + "from Hit as h join fetch App as a on a.id = h.app.id "
            + "where (h.timestamp between :start and :end) "
            + "and ((h.uri in :uris) or (coalesce(:uris, '') = '')) "
            + "group by a.name, h.uri "
            + "order by count(h.ip) desc")
    List<StatsResponseDto> findNonUniqHits(LocalDateTime start, LocalDateTime end, List<String> uris);
}