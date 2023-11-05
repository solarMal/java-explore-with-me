package ru.practicum.stat;

import ru.practicum.event.entity.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatService {

    Map<Long, Long> getStats(List<Event> event);

    void hit(HttpServletRequest request);
}