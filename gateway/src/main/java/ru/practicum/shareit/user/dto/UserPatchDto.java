package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.NullableNotBlank;

import javax.validation.constraints.Email;

@Value
public class UserPatchDto {

    @NullableNotBlank
    String name;

    @Email
    @NullableNotBlank
    String email;
}
