package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.CustomValidationException;

public enum BookingSearchState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingSearchState of(String name) {
        try {
            return BookingSearchState.valueOf(name);
        } catch (IllegalArgumentException exception) {
            throw new CustomValidationException("Unknown state: " + name);
        }
    }
}
