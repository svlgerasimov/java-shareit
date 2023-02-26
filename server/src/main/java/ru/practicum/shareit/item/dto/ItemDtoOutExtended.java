package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoShort;

import java.util.List;

@Value
public class ItemDtoOutExtended {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;
    List<CommentDtoOut> comments;
}
