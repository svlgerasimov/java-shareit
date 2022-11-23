package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Value
public class ItemDto {
    Long id;

//    @NotNull(groups = ValidatedFull.class)
    String name;

//    @NotNull(groups = ValidatedFull.class)
    String description;

//    @NotNull(groups = ValidatedFull.class)
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

//    // Группа валидации при добавлении
//    public interface ValidatedFull {}
//
//    // Группа валидации при patch-запросе
//    public interface ValidatedPatch {}
}
