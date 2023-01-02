package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto dto, long userId);

    ItemDto patch(long itemId, ItemPatchDto dto, long userId);

    ItemDtoOutExtended getById(long id, long userId);

    List<ItemDtoOutExtended> getAll(long userId, long from, Integer size);

    List<ItemDto> search(String text, long from, Integer size);

    CommentDtoOut addComment(CommentDtoIn dto, long itemId, long userId);
}
