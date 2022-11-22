package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Objects;

@Value
public class UserDto {
    // id должен отсутствовать в теле запроса.
    // При добавлении он не нужен, при обновлении - передается в параметре запроса
    @Null(groups = {ValidatedFull.class, ValidatedPatch.class})
    Long id;

    String name;

    // Почта обязательно должна быть при добавлении, в patch-запросе может отсутствовать
    @NotNull(groups = ValidatedFull.class)
    @Email(groups = {ValidatedFull.class, ValidatedPatch.class})
    String email;

    // Группа валидации при добавлении
    public interface ValidatedFull {}

    // Группа валидации при patch-запросе
    public interface ValidatedPatch {}
}
