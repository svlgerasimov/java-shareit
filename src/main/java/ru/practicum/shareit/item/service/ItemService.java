package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOutWithBookings;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto dto, long userId);

    ItemDto patch(long itemId, ItemPatchDto dto, long userId);

    ItemDtoOutWithBookings getById(long id, long userId);

    List<ItemDtoOutWithBookings> getAll(long userId);

    List<ItemDto> search(String text);
}
