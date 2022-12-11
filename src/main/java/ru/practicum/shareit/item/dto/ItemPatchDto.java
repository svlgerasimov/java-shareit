package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.NullableNotBlank;

@Value
public class ItemPatchDto {

    @NullableNotBlank
    String name;

    @NullableNotBlank
    String description;

    Boolean available;
}
