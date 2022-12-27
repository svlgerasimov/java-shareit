package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @PostMapping
    public ItemRequestDtoOut add(@RequestBody @Valid ItemRequestDtoIn itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return null;
    }


}
