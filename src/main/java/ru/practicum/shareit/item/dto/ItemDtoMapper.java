package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {

    ItemDto toDto(Item item);


    Item fromDto(ItemDto dto);
}
