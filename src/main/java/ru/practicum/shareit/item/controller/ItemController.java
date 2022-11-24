package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) {
        return itemService.getById(id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
//    public ItemDto add(@RequestBody @Validated(ItemDto.ValidatedFull.class) ItemDto itemDto,
//                       @RequestHeader("X-Sharer-User-Id") long userId) {
    public ItemDto add(@RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        fullValidateItemDto(itemDto);
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{id}")
//    public ItemDto patch(@PathVariable long id,
//                         @RequestBody @Validated(ItemDto.ValidatedPatch.class) ItemDto patchDto,
//                         @RequestHeader("X-Sharer-User-Id") long userId) {
    public ItemDto patch(@PathVariable long id,
                         @RequestBody ItemDto patchDto,
                         @RequestHeader("X-Sharer-User-Id") long userId) {
        patchValidateItemDto(patchDto);
        return itemService.patch(id, patchDto, userId);
    }

    private void fullValidateItemDto(ItemDto itemDto) {
        if (!itemDto.hasName()) {
            throw new CustomValidationException("No 'name' field");
        } else if (itemDto.getName().isBlank()) {
            throw new CustomValidationException("'name' field is blank");
        }
        if (!itemDto.hasDescription()) {
            throw new CustomValidationException("No 'description' field");
        } else if (itemDto.getDescription().isBlank()) {
            throw new CustomValidationException("'description' field is blank");
        }
        if (!itemDto.hasAvailable()) {
            throw new CustomValidationException("No 'available' field");
        }
    }

    private void patchValidateItemDto(ItemDto itemDto) {
        if (itemDto.hasName() && itemDto.getName().isBlank()) {
            throw new CustomValidationException("'name' field is blank");
        }
        if (itemDto.hasDescription() && itemDto.getDescription().isBlank()) {
            throw new CustomValidationException("'description' field is blank");
        }
    }

}
