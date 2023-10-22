package service;

import entity.App;
import entity.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AppRepository;
import repository.HitRepository;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mapper.HitMapper.toHit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public void saveHit(StatsRequestDto statsRequestDto) {
        App app = appRepository.findByName(statsRequestDto.getApp())
                .orElseGet(() -> createAndSaveApp(statsRequestDto.getApp()));

        Hit hit = toHit(statsRequestDto, app);
        hitRepository.save(hit);

        log.info("Hit сохранён успешно");
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null) {
            uris = Collections.emptyList();
        }

        List<StatsResponseDto> statsResponse;

        if (unique) {
            statsResponse = hitRepository.findUniqueHits(start, end, uris);
            log.info("Получение данных о хитах от уникальных IP-адресов завершено успешно");
        } else {
            statsResponse = hitRepository.findNonUniqueHits(start, end, uris);
            log.info("Получение данных о хитах завершено успешно");
        }

        return statsResponse;
    }

    private App createAndSaveApp(String appName) {
        App app = new App();
        app.setName(appName);
        return appRepository.save(app);
    }
}