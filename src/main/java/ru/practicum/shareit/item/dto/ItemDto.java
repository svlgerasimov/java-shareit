package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {
    Long id;

    @NotNull(groups = ValidatedFull.class)
    String name;

    @NotNull(groups = ValidatedFull.class)
    String description;

    @NotNull(groups = ValidatedFull.class)
    Boolean available;

    // Группа валидации при добавлении
    public interface ValidatedFull {}

    // Группа валидации при patch-запросе
    public interface ValidatedPatch {}
}
