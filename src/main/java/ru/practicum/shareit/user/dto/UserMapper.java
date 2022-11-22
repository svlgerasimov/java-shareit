package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.Objects;

public class UserMapper {

    private UserMapper() {

    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                // если имя не указано, можно отображать вместо него почту;
                // писать в хранилище почту вместо имени для этого незачем
                Objects.requireNonNullElse(user.getName(), user.getEmail()),
                user.getEmail()
        );
    }

    public static User fromUserDto(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static User patchWithUserDto(User user, UserDto patchDto) {
        User patchedUser = new User();
        patchedUser.setId(user.getId());
        patchedUser.setEmail(
                Objects.requireNonNullElse(patchDto.getEmail(), user.getEmail()));
        patchedUser.setName(
                Objects.requireNonNullElse(patchDto.getName(), user.getName()));
        return patchedUser;
    }
}
