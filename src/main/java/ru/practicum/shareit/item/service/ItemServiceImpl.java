package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final ItemPatchDtoMapper itemPatchDtoMapper;
    private final CommentDtoMapper commentDtoMapper;

    @Override
    @Transactional
    public ItemDto add(ItemDto dto, long userId) {
        User owner = getUser(userId);
        Item item = itemDtoMapper.fromDto(dto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        log.debug("Add item " + item);
        return itemDtoMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto patch(long itemId, ItemPatchDto dto, long userId) {
        Item item = getItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new AuthenticationErrorException("User id=" + userId + " is not owner of item id=" + itemId);
        }
        itemPatchDtoMapper.updateWithPatchDto(item, dto);
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

    @Override
    public List<ItemDtoOutExtended> getAll(long userId) {
        User owner = getUser(userId);
        List<Item> items = itemRepository.findAllByOwner(owner, Sort.by(Sort.Direction.ASC, "id"));
        List<Comment> comments = commentRepository.findAllByItemIn(items);
        Map<Item, List<Comment>> commentsByItems = comments.stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository.findAllByItemInAndStartLessThanEqualAndStatusIs(
                items, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
        Map<Item, Booking> lastBookingsByItems = lastBookings.stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (booking1, booking2) -> booking1));
        List<Booking> nextBookings = bookingRepository.findAllByItemInAndStartAfterAndStatusIs(
                items, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
        Map<Item, Booking> nextBookingsByItems = nextBookings.stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (booking1, booking2) -> booking1));

        return items.stream()
                .map(item -> itemDtoMapper.toDtoExtended(item, commentsByItems.get(item),
                        lastBookingsByItems.get(item), nextBookingsByItems.get(item)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text.toLowerCase()).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

    private ItemDtoOutExtended formDtoExtended(Item item) {
        List<Comment> comments = commentRepository.findAllByItem(item);
        return itemDtoMapper.toDtoExtended(item, comments);
    }

    private ItemDtoOutExtended formDtoExtendedWithBookings(Item item) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository
                .findFirstByItemAndStartLessThanEqualAndStatusIs(
                        item, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .orElse(null);
        Booking nextBooking = bookingRepository
                .findFirstByItemAndStartAfterAndStatusIs(
                        item, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .orElse(null);
        List<Comment> comments = commentRepository.findAllByItem(item);
        return itemDtoMapper.toDtoExtended(item, comments, lastBooking, nextBooking);
    }
}
