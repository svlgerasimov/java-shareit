package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        return text.isBlank() ? ResponseEntity.ok(Collections.emptyList()) : itemClient.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.add(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@PathVariable long id,
                         @RequestBody @Valid ItemPatchDto patchDto,
                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.patch(id, patchDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDtoIn dto,
                                    @PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.addComment(dto, itemId, userId);
    }
}
