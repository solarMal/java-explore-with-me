package ru.practicum.stat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsResponseDto;
import ru.practicum.event.entity.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Map<Long, Long> getStats(List<Event> events) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start;
        List<String> uris = new ArrayList<>();
        Map<Long, Long> result = new HashMap<>();
        for (Event e : events) {
            if (e.getPublishedOn() != null && e.getPublishedOn().isBefore(start)) {
                start = e.getPublishedOn();
            }
            uris.add("/events/" + e.getId());
        }
        ResponseEntity<Object> response = statsClient.get(start.format(FORMATTER), end.format(FORMATTER), uris, true);
        List<StatsResponseDto> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (!stats.isEmpty()) {
            for (StatsResponseDto s : stats) {
                String[] event = s.getUri().split("/");
                result.put(Long.parseLong(event[event.length - 1]), s.getHits());
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void hit(HttpServletRequest request) {
        log.info("Trying do add new hit to stat");
        statsClient.hit(request, "ewm-main-service");
    }
}