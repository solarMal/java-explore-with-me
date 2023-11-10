package ru.practicum.request.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestsCount {

    private long eventId;
    private long count;
}