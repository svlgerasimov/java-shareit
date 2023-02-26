package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingDtoShortMapper {
    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoShort toDtoShort(Booking booking);
}
