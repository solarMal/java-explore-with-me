package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsResponseDto;
import ru.practicum.entity.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.StatsResponseDto(a.name, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "JOIN FETCH App a ON a.id = h.app.id " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (h.uri IN :uris OR COALESCE(:uris, '') = '') " +
            "GROUP BY a.name, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<StatsResponseDto> findUniqueHits(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new ru.practicum.StatsResponseDto(a.name, h.uri, COUNT(h.ip)) " +
            "FROM Hit h " +
            "JOIN FETCH App a ON a.id = h.app.id " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (h.uri IN :uris OR COALESCE(:uris, '') = '') " +
            "GROUP BY a.name, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<StatsResponseDto> findNonUniqueHits(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}