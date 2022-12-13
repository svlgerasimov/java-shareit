package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoShortMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {BookingDtoShortMapper.class, CommentDtoMapper.class})
public interface ItemDtoMapper {

    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "item.id")
    ItemDtoOutExtended toDtoExtended(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    ItemDtoOutExtended toDtoExtended(Item item, List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromDto(ItemDto dto);
}
