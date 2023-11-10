package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.request.dto.RequestDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateResponse {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}