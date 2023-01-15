package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.NullableNotBlank;

import javax.validation.constraints.Email;

@Value
public class UserPatchDto {

    String name;

    String email;
}
