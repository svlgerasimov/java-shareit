package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutExtended;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut add(@RequestBody ItemRequestDtoIn itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoOutExtended> findByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOutExtended> findAllByOtherUsers(
            @RequestParam Long from,
            @RequestParam Integer size,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOutExtended findById(@PathVariable long requestId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findById(requestId, userId);
    }





}
