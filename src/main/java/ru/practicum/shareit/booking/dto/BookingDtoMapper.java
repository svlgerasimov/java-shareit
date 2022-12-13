package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserDtoMapper.class, ItemDtoMapper.class})
public interface BookingDtoMapper {

    BookingDtoOut toDto(Booking booking);

    List<BookingDtoOut> toDto(List<Booking> bookings);

    @Mapping(target = "status", constant = "WAITING")
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", ignore = true)
    Booking fromDto(BookingDtoIn dto);

}
