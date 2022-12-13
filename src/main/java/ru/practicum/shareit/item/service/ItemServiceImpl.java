package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AuthenticationErrorException;
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final ItemPatchDtoMapper itemPatchDtoMapper;
    private final CommentDtoMapper commentDtoMapper;

    @Override
    public ItemDto add(ItemDto dto, long userId) {
        User owner = getUser(userId);
        Item item = itemDtoMapper.fromDto(dto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        log.debug("Add item " + item);
        return itemDtoMapper.toDto(item);
    }

    @Override
    public ItemDto patch(long itemId, ItemPatchDto dto, long userId) {
        Item item = getItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthenticationErrorException("User id=" + userId + " is not owner of item id=" + itemId);
        }
        itemPatchDtoMapper.updateWithPatchDto(item, dto);
        item = itemRepository.save(item);
        log.debug("Patch item " + item);
        return itemDtoMapper.toDto(item);
    }

    @Override
    public ItemDtoOutExtended getById(long id, long userId) {
        getUser(userId);
        Item item = getItem(id);
        return item.getOwner().getId().equals(userId) ?
                formDtoExtendedWithBookings(item) :
                formDtoExtended(item);
    }

    private ItemDtoOutExtended formDtoExtended(Item item) {
        List<Comment> comments = commentRepository.findAllByItem(item);
        return itemDtoMapper.toDtoExtended(item, comments);
    }

    private ItemDtoOutExtended formDtoExtendedWithBookings(Item item) {
        List<Comment> comments = commentRepository.findAllByItem(item);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository
                .findFirstByItemAndStartBeforeOrderByStartDesc(item, now)
                .orElse(null);
        Booking nextBooking = bookingRepository
                .findFirstByItemAndStartAfterOrderByStartDesc(item, now)
                .orElse(null);
        return itemDtoMapper.toDtoExtended(item, comments, lastBooking, nextBooking);
    }

    @Override
    public List<ItemDtoOutExtended> getAll(long userId) {
        User owner = getUser(userId);
        return itemRepository.findAllByOwner(owner).stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .map(this::formDtoExtendedWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text.toLowerCase()).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoOut addComment(CommentDtoIn dto, long itemId, long userId) {
        Item item = getItem(itemId);
        User user = getUser(userId);
        bookingRepository.findFirstByItemAndBookerAndEndBefore(item, user, LocalDateTime.now())
                .orElseThrow(() -> new CustomValidationException(
                        "User id=" + userId + " doesnt have finished booking of item id=" + itemId));
        Comment comment = commentDtoMapper.fromDto(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        log.debug("Add comment: " + comment);
        return commentDtoMapper.toDto(comment);
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=" + itemId + " not found"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }
}
