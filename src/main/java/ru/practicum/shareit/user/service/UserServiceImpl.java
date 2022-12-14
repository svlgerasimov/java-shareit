package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final UserPatchDtoMapper userPatchDtoMapper;

    @Override
    @Transactional
    public UserDto add(UserDto dto) {
        User user = userRepository.save(userDtoMapper.fromDto(dto));
        log.debug("Add user " + user);
        return userDtoMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto patch(long id, UserPatchDto patchDto) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("User with id=" + id + " not found")
                );
        String patchEmail = patchDto.getEmail();
        if (Objects.nonNull(patchEmail)) {
            // если пришёл патч запрос с той же почтой, что и была раньше - ничего страшного
            userRepository.findByEmailAndIdIsNot(patchEmail, id).ifPresent(
                    foundUser -> {
                        throw new ConflictException("User with email='" + patchEmail + "' already exists");
                    });
        }
        userPatchDtoMapper.updateWithPatchDto(user, patchDto);
        user = userRepository.save(user);
        log.debug("Patch user " + user);
        return userDtoMapper.toDto(user);
    }

    @Override
    @Transactional
    public void remove(long id) {
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
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        return userDtoMapper.toDto(
                userRepository.findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("User with id=" + id + " not found")
                        )
        );
    }
}
