package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserServiceImpl implements UserService {
//    private final UserStorage userStorage;
    private final UserRepository userRepository;

    @Override
    public UserDto add(UserDto dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(
                foundUser -> {
                    throw new ConflictException("User with email='" + dto.getEmail() + "' already exists");
                });
//        User user = userStorage.add(UserMapper.fromUserDto(dto));
        User user = userRepository.save(UserMapper.fromUserDto(dto));
        log.debug("Add user " + user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patch(long id, UserDto patchDto) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User with id=" + id + " not found")
                );
        String patchEmail = patchDto.getEmail();
        if (Objects.nonNull(patchEmail)) {
            // если пришёл патч запрос с той же почтой, что и была раньше - ничего страшного
//            userStorage.getByEmailExcludeId(patchEmail, id).ifPresent(
            userRepository.findByEmailAndIdIsNot(patchEmail, id).ifPresent(
                    foundUser -> {
                        throw new ConflictException("User with email='" + patchEmail + "' already exists");
                    });
        }
        UserMapper.patchWithUserDto(user, patchDto);
        user = userRepository.save(user);
        log.debug("Patch user " + user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void remove(long id) {
//        if (userRepository.deleteById(id) < 1) {
//            throw new NotFoundException("User with id=" + id + " not found");
//        }
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.warn(e.getMessage(), e);
            throw new NotFoundException("User with id=" + id + " not found");
        }
        log.debug("Remove user id=" + id);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return UserMapper.toUserDto(
                userRepository.findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("User with id=" + id + " not found")
                        )
        );
    }
}
