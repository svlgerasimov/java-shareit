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
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    // Заменять пользователя в хранилище - плохая идея,
    // потому что тогда нужно было бы следить за ссылками на него в других классах, в частности в Item.
    // Поэтому здесь вносим изменения в существующий объект User, а не возвращаем новый
    public static void patchWithUserDto(User userToPatch, UserDto patchDto) {
        String patchEmail = patchDto.getEmail();
        if (Objects.nonNull(patchEmail)) {
            userToPatch.setEmail(patchEmail);
        }
        String patchName = patchDto.getName();
        if (Objects.nonNull(patchName)) {
            userToPatch.setName(patchName);
        }
    }
}
