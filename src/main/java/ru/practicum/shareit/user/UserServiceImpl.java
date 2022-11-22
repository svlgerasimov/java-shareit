package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StorageWriteException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Validated
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
//    private final Validator validator;

    @Override
    public UserDto add(UserDto dto) {
        userStorage.getByEmail(dto.getEmail()).ifPresent(
                foundUser -> {
                    throw new ConflictException("User with email='" + dto.getEmail() + "' already exists");
                });
        return UserMapper.toUserDto(
                userStorage.add(UserMapper.fromUserDto(dto))
        );
    }

    @Override
    public UserDto patch(long id, UserDto patchDto) {
        User user = userStorage.getById(id)
                .orElseThrow(
                        () -> new NotFoundException("User with id=" + id + " not found")
                );
        String patchEmail = patchDto.getEmail();
        if (Objects.nonNull(patchEmail)) {
            // если пришёл патч запрос с той же почтой, что и раньше - ничего страшного
            userStorage.getByEmailExcludeId(patchEmail, id).ifPresent(
                    foundUser -> {
                        throw new ConflictException("User with email='" + patchEmail + "' already exists");
                    });
        }
        user = UserMapper.patchWithUserDto(user, patchDto);
        if (!userStorage.update(user)) {
            // вообще в данной реализации такого конечно быть не может
            throw new StorageWriteException("User " + user + " was not updated in storage");
        }
        return UserMapper.toUserDto(user);
    }

//    @Override
////    public UserDto update(@Valid UserDto dto) {
//    public UserDto update(UserDto dto) {
//        if(!userStorage.update(UserMapper.fromUserDto(dto))) {
//            throw new NotFoundException("User with id=" + dto.getId() + " not found");
//        }
//        return dto;
//    }

    @Override
    public void remove(long id) {
        if(!userStorage.remove(id)) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
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
