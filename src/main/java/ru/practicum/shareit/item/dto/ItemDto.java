package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.util.Objects;

@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;

    public boolean hasName() {
        return Objects.nonNull(name);
    }

    public boolean hasDescription() {
        return Objects.nonNull(description);
    }

    public boolean hasAvailable() {
        return Objects.nonNull(available);
    }
}
