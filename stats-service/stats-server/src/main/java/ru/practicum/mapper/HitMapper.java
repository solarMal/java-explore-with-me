package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatsRequestDto;
import ru.practicum.entity.App;
import ru.practicum.entity.Hit;

@UtilityClass
public class HitMapper {

    public static Hit toHit(StatsRequestDto statsRequestDto, App app) {
        Hit hit = new Hit();
        hit.setApp(app);
        hit.setUri(statsRequestDto.getUri());
        hit.setIp(statsRequestDto.getIp());
        hit.setTimestamp(statsRequestDto.getTimestamp());
        return hit;
    }
}