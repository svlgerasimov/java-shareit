package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDtoOutExtended getById(@PathVariable long id,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDtoOutExtended> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "0") Long from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") Long from,
                                @RequestParam(defaultValue = "10") Integer size) {
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@PathVariable long id,
                         @RequestBody ItemPatchDto patchDto,
                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.patch(id, patchDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@RequestBody CommentDtoIn dto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addComment(dto, itemId, userId);
    }
}
