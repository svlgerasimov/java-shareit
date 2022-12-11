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
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserPatchDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemServiceImpl implements ItemService {
//    private final ItemStorage itemStorage;
//    private final UserStorage userStorage;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto add(ItemDto dto, long userId) {
        User owner = getOwner(userId);
        Item item = ItemMapper.fromItemDto(dto);
        item.setOwner(owner);
//        item = itemStorage.add(item);
        item = itemRepository.save(item);
        log.debug("Add item " + item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto patch(long itemId, ItemDto dto, long userId) {
//        Item item = itemStorage.getById(itemId)
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthenticationErrorException("User id=" + userId + " is not owner of item id=" + itemId);
        }
        ItemMapper.patchFromDto(item, dto);
        item = itemRepository.save(item);
        log.debug("Patch item " + item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(long id) {
        return ItemMapper.toItemDto(
//                itemStorage.getById(id)
                itemRepository.findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("Item with id=" + id + " not found")
                        )
        );
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        User owner = getOwner(userId);
//        return itemStorage.getAll(owner).stream()
        return itemRepository.findAllByOwner(owner).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
//        return itemStorage.search(text).stream()
        return itemRepository.search(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User getOwner(long userId) {
//        return userStorage.getById(userId)
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }
}
