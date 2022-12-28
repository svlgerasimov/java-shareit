package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutExtended;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @PostMapping
    public ItemRequestDtoOut add(@RequestBody @Valid ItemRequestDtoIn itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return null;
    }

    @GetMapping
    public List<ItemRequestDtoOutExtended> findByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return null;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutExtended> findAllByOtherUsers(@RequestParam long from, @RequestParam int size,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return null;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutExtended findById(long requestId) {
        return null;
    }





}
