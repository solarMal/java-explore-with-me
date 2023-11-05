package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestDto {

    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private String created;

    private long event;
    private long requester;
    private String status;
}