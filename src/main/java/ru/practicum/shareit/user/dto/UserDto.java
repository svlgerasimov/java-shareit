package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Value
public class UserDto {
    Long id;

    String name;

    @Email(groups = {FullValidated.class, PatchValidated.class})
    @NotNull(groups = FullValidated.class)
    String email;

    public interface FullValidated {

    }

    public interface PatchValidated {

    }
}
