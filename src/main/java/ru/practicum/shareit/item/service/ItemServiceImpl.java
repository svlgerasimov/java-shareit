package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AuthenticationErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(ItemDto dto, long userId) {
        User owner = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Item item = ItemMapper.fromItemDto(dto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemStorage.add(item));
    }

    @Override
    public ItemDto patch(long itemId, ItemDto dto, long userId) {
        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthenticationErrorException("User id=" + userId + " is not owner of item id=" + itemId);
        }
        ItemMapper.patchFromDto(item, dto);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(long id) {
        return ItemMapper.toItemDto(
                itemStorage.getById(id)
                        .orElseThrow(
                                () -> new NotFoundException("Item with id=" + id + " not found")
                        )
        );
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        User owner = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return itemStorage.getAll(owner).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
