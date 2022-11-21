package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Value
public class UserDto {
    Long id;
    String name;
    @NotNull
    @Email
    String email;

    public UserDto patch(UserDto patchDto) {
        return new UserDto(
                id,
                Objects.requireNonNullElse(patchDto.getName(), name),
                Objects.requireNonNullElse(patchDto.getEmail(), email)
        );
    }
}
