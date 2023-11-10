package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.event.entity.Location;

@UtilityClass
public class LocationMapper {

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}