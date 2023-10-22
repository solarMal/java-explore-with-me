package ru.practicum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatsResponseDto {

    //Название сервиса
    private String app;

    //URI сервиса
    private String uri;

    //Количество просмотров
    private Long hits;

    public StatsResponseDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
