package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class ItemRequestDtoIn {

    @NotBlank
    String description;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ItemRequestDtoIn(@JsonProperty("description") String description) {
        this.description = description;
    }
}
