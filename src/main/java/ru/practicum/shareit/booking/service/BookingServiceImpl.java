package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingSearchState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoMapper bookingDtoMapper;

    @Override
    @Transactional
    public BookingDtoOut add(BookingDtoIn dto, long userId) {
        Item item = itemRepository.findByIdAndOwnerIdNot(dto.getItemId(), userId)
                .orElseThrow(() -> new NotFoundException(
                        "Item with id=" + dto.getItemId() + " and owner id other then " + userId + " not found"));
        if (!item.getAvailable()) {
            throw new CustomValidationException("Item id=" + item.getId() + " is not available");
        }
        User booker = getUser(userId);
        Booking booking = bookingDtoMapper.fromDto(dto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking = bookingRepository.save(booking);
        log.debug("Add booking " + booking);
        return bookingDtoMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut approve(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Booking with id=" + bookingId + " and owner id=" + userId + " not found"));
        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
            throw new CustomValidationException("Booking already has been approved/rejected");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        log.debug("Approved booking " + booking);
        return bookingDtoMapper.toDto(booking);
    }

    @Override
    public BookingDtoOut findById(long bookingId, long userId) {
        Booking booking = bookingRepository.findByIdAndItemOwnerIdOrBookerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Booking with id=" + bookingId + " and owner or booker id=" + userId + " not found"));
        return bookingDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingDtoOut> findByBooker(long bookerId, BookingSearchState state) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findByBookerAndEndIsBefore(booker, now,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerAndStartIsAfter(booker, now,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(
                        booker, now, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBooker(booker, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return bookingDtoMapper.toDto(bookings);
    }

    @Override
    public List<BookingDtoOut> findByOwner(long ownerId, BookingSearchState state) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findByItemOwnerAndEndIsBefore(owner, now,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerAndStartIsAfter(owner, now,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(
                        owner, now, now, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByItemOwner(owner, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return bookingDtoMapper.toDto(bookings);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }
}
