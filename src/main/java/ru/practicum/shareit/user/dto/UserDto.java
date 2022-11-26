package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.NullableNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
public class UserDto {
    Long id;

    @NotBlank(groups = FullValidated.class)
    @NullableNotBlank(groups = PatchValidated.class)
    String name;

    @Email(groups = {FullValidated.class, PatchValidated.class})
    @NotNull(groups = FullValidated.class)
    String email;

    public interface FullValidated {

    }

    public interface PatchValidated {

    }
}
