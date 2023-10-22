package mapper;

import entity.App;
import entity.Hit;
import lombok.experimental.UtilityClass;
import ru.practicum.StatsRequestDto;

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
