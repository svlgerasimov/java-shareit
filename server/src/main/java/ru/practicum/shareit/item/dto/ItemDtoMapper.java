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

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toDto(Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoOutExtended toDtoExtended(Item item, List<Comment> comments, Booking lastBooking, Booking nextBooking);

    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoOutExtended toDtoExtended(Item item, List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item fromDto(ItemDto dto);
}
