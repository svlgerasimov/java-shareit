package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto dto, long userId);

    ItemDto patch(long itemId, ItemDto dto, long userId);

    ItemDto getById(long id);

    List<ItemDto> getAll(long userId);

    List<ItemDto> search(String text);
}
