package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

import java.util.Objects;

public class UserMapper {

    private UserMapper(){

    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
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

//    public static User patchFromUserDto(User user, UserDto patchDto) {
//        String patchEmail = patchDto.getEmail();
//        if (Objects.nonNull(patchEmail)) {
//            user.setEmail(patchEmail);
//        }
//        String patchName = patchDto.getName();
//        if (Objects.nonNull(patchName)) {
//            user.setName(patchName);
//        }
//        return user;
//    }
}
