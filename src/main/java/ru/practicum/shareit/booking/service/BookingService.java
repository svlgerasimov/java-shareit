package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingSearchState;

import java.util.List;

public interface BookingService {
    BookingDtoOut add(BookingDtoIn dto, long userId);

    BookingDtoOut approve(long bookingId, long userId, boolean approved);

    BookingDtoOut findById(long bookingId, long userId);

    List<BookingDtoOut> findByBooker(long bookerId, BookingSearchState state, long from, Integer size);

    List<BookingDtoOut> findByOwner(long ownerId, BookingSearchState state, long from, Integer size);
}
