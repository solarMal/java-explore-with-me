package ru.practicum.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.request.entity.Request;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }
}