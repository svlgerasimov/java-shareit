package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(ItemDto dto, long userId) {
        User owner = getOwner(userId);
        Item item = ItemMapper.fromItemDto(dto);
        item.setOwner(owner);
        item = itemStorage.add(item);
        log.debug("Add item " + item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patch(long itemId, ItemDto dto, long userId) {
        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthenticationErrorException("User id=" + userId + " is not owner of item id=" + itemId);
        }
        ItemMapper.patchFromDto(item, dto);
        log.debug("Patch item " + item);
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
        User owner = getOwner(userId);
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

    private User getOwner(long userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }
}
