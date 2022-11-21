package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

public interface UserService {
    UserDto add(UserDto dto);

//    UserDto patch(long id, UserDto patchDto);
//    UserDto update(@Valid UserDto dto);
    UserDto update(UserDto dto);

    void remove(long id);
    List<UserDto> getAll();
    UserDto getById(long id);
}
