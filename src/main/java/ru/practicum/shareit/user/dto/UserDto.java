package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import java.util.Objects;

@Value
public class UserDto {
    Long id;
    String name;
    @Email
    String email;

    public boolean hasEmail() {
        return Objects.nonNull(email);
    }
}
