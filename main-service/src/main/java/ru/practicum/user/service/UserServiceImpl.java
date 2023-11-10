package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.user.dto.UserMapper.toUser;
import static ru.practicum.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(toUser(userDto));
        log.info("User with ID: '" + user.getId() + "' successfully created");
        return toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<UserDto> result;
        if (ids == null) {
            result = userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            result = userRepository.findByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        log.info("{} users found by request", result.size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID: '" + id + "'not found"));
        userRepository.delete(user);
        log.info("User with ID: '{}' successfully removed", user.getId());
    }
}