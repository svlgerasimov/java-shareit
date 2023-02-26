package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class CommentDtoIn {

    @NotBlank
    String text;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CommentDtoIn(@JsonProperty("text") String text) {
        this.text = text;
    }
}
