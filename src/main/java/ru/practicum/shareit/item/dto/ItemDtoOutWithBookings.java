package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

@Value
public class ItemDtoOutWithBookings {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;
}
