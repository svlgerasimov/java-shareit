package ru.practicum.shareit.request.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ItemRequestDtoOut {

    Long id;

    String description;

    LocalDateTime created;
}
