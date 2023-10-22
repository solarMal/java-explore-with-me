package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsResponseDto;
import ru.practicum.entity.App;
import ru.practicum.entity.Hit;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.mapper.HitMapper.toHit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public void saveHit(StatsRequestDto statsRequestDto) {
        Optional<App> optApp = appRepository.findByName(statsRequestDto.getApp());
        App app = new App();
        if (optApp.isEmpty()) {
            app.setName(statsRequestDto.getApp());
            appRepository.save(app);
        } else {
            app = optApp.get();
        }
        Hit hit = toHit(statsRequestDto, app);
        hitRepository.save(hit);
        log.info("Hit saving complete successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null) {
            uris = Collections.emptyList();
        }
        if (unique) {
            log.info("Hits from unique IP's receiving complete successfully");
            return hitRepository.findUniqHits(start, end, uris);
        } else {
            log.info("Hits receiving complete successfully");
            return hitRepository.findNonUniqHits(start, end, uris);
        }
    }
}