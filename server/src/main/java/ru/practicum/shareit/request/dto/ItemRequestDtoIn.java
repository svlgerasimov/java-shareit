package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ItemRequestDtoIn {

    String description;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ItemRequestDtoIn(@JsonProperty("description") String description) {
        this.description = description;
    }
}
