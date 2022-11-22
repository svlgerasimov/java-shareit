package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * TODO Sprint add-controllers.
 */
public class ItemDto {
    // id должен отсутствовать в теле запроса.
    // При добавлении он не нужен, при обновлении - передается в параметре запроса
    @Null(groups = {UserDto.ValidatedFull.class, UserDto.ValidatedPatch.class})
    Long id;

    String name;

    // Почта обязательно должна быть при добавлении, в patch-запросе может отсутствовать
    @NotNull(groups = UserDto.ValidatedFull.class)
    @Email(groups = {UserDto.ValidatedFull.class, UserDto.ValidatedPatch.class})
    String email;

    // Группа валидации при добавлении
    public interface ValidatedFull {}

    // Группа валидации при patch-запросе
    public interface ValidatedPatch {}
}
