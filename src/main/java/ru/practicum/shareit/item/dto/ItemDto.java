package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {
    @Null(groups = {UserDto.ValidatedFull.class, UserDto.ValidatedPatch.class})
    Long id;

    @NotNull(groups = UserDto.ValidatedFull.class)
    String name;

    String description;

    @NotNull(groups = UserDto.ValidatedFull.class)
    Boolean available;

    // Группа валидации при добавлении
    public interface ValidatedFull {}

    // Группа валидации при patch-запросе
    public interface ValidatedPatch {}
}
