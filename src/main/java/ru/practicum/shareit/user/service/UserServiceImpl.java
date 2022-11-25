package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
//@Validated
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto dto) {
        userStorage.getByEmail(dto.getEmail()).ifPresent(
                foundUser -> {
                    throw new ConflictException("User with email='" + dto.getEmail() + "' already exists");
                });
        User user = userStorage.add(UserMapper.fromUserDto(dto));
        log.debug("Add user " + user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patch(long id, UserDto patchDto) {
        User user = userStorage.getById(id)
                .orElseThrow(
                        () -> new NotFoundException("User with id=" + id + " not found")
                );
        String patchEmail = patchDto.getEmail();
        if (Objects.nonNull(patchEmail)) {
            // если пришёл патч запрос с той же почтой, что и была раньше - ничего страшного
            userStorage.getByEmailExcludeId(patchEmail, id).ifPresent(
                    foundUser -> {
                        throw new ConflictException("User with email='" + patchEmail + "' already exists");
                    });
        }
        UserMapper.patchWithUserDto(user, patchDto);
        log.debug("Patch user " + user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void remove(long id) {
        if (!userStorage.remove(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        log.debug("Remove user id=" + id);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return UserMapper.toUserDto(
                userStorage.getById(id)
                        .orElseThrow(
                                () -> new NotFoundException("User with id=" + id + " not found")
                        )
        );
    }
}
