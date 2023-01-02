package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import java.util.Objects;

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
    public List<BookingDtoOut> findByBooker(long bookerId, BookingSearchState state, long from, Integer size) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case PAST:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBookerAndEndIsBefore(booker, now, sort) :
                        bookingRepository.findByBookerAndEndIsBefore(booker, now, formPageable(from, size, sort));
                break;
            case FUTURE:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBookerAndStartIsAfter(booker, now, sort) :
                        bookingRepository.findByBookerAndStartIsAfter(booker, now, formPageable(from, size, sort));
                break;
            case CURRENT:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(booker, now, now, sort) :
                        bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(booker, now, now,
                                formPageable(from, size, sort));
                break;
            case WAITING:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.WAITING, sort) :
                        bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.WAITING,
                                formPageable(from, size, sort));
                break;
            case REJECTED:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.REJECTED, sort) :
                        bookingRepository.findByBookerAndStatusIs(booker, BookingStatus.REJECTED,
                                formPageable(from, size, sort));
                break;
            case ALL:
            default:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByBooker(booker, sort) :
                        bookingRepository.findByBooker(booker, formPageable(from, size, sort));
                break;
        }
        return bookingDtoMapper.toDto(bookings);
    }

    @Override
    public List<BookingDtoOut> findByOwner(long ownerId, BookingSearchState state, long from, Integer size) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case PAST:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwnerAndEndIsBefore(owner, now, sort) :
                        bookingRepository.findByItemOwnerAndEndIsBefore(owner, now, formPageable(from, size, sort));
                break;
            case FUTURE:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwnerAndStartIsAfter(owner, now, sort):
                        bookingRepository.findByItemOwnerAndStartIsAfter(owner, now, formPageable(from, size, sort));
                break;
            case CURRENT:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(owner, now, now, sort) :
                        bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(owner, now, now,
                                formPageable(from, size, sort));
                break;
            case WAITING:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.WAITING, sort) :
                        bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.WAITING,
                                formPageable(from, size, sort));
                break;
            case REJECTED:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.REJECTED, sort) :
                        bookingRepository.findByItemOwnerAndStatusIs(owner, BookingStatus.REJECTED,
                                formPageable(from, size, sort));
                break;
            case ALL:
            default:
                bookings = Objects.isNull(size) ?
                        bookingRepository.findByItemOwner(owner, sort) :
                        bookingRepository.findByItemOwner(owner, formPageable(from, size, sort));
                break;
        }
        return bookingDtoMapper.toDto(bookings);
    }

    private static PageRequest formPageable(long from, Integer size, Sort sort) {
        return PageRequest.of((int) (from / size), size, sort);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }
}
