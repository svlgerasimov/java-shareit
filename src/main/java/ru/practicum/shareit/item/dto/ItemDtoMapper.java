package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoShortMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BookingDtoShortMapper.class})
public interface ItemDtoMapper {

    ItemDto toDto(Item item);

//    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "id", source = "item.id")
    ItemDtoOutWithBookings toDtoWithBookings(Item item, Booking lastBooking, Booking nextBooking);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    ItemDtoOutWithBookings toDtoWithBookings(Item item);

//    List<ItemDtoOutWithBookings> toDtoWithBookings(List<Item> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromDto(ItemDto dto);
}
