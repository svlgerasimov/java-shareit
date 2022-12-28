package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemDtoMapper.class)
public interface ItemRequestDtoMapper {

    ItemRequestDtoOut toDto(ItemRequest itemRequest);


    ItemRequestDtoOutExtended toExtendedDto(ItemRequest itemRequest);

    List<ItemRequestDtoOutExtended> toExtendedDto(List<ItemRequest> itemRequests);

    ItemRequest fromDto(ItemRequestDtoIn dto);
}
