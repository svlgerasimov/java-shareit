package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.NullableNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class UserDto {
    Long id;

//    @NotBlank(groups = FullValidated.class)
//    @NullableNotBlank(groups = PatchValidated.class)
    @NotBlank
    String name;

//    @Email(groups = {FullValidated.class, PatchValidated.class})
//    @NotBlank(groups = FullValidated.class)
//    @NullableNotBlank(groups = PatchValidated.class)
    @Email
    @NotBlank
    String email;

//    public interface FullValidated {
//
//    }
//
//    public interface PatchValidated {
//
//    }
}
