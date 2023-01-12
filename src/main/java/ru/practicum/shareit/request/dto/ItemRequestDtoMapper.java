package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ItemDtoMapper.class)
public interface ItemRequestDtoMapper {

    ItemRequestDtoOut toDto(ItemRequest itemRequest);

    ItemRequestDtoOutExtended toExtendedDto(ItemRequest itemRequest, List<Item> items);

    @Named("toExtendedDto")
    default List<ItemRequestDtoOutExtended> toExtendedDto(List<ItemRequest> itemRequests,
                                                                Map<Long, List<Item>> itemsByRequestId) {
        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemsByRequestId.get(itemRequest.getId());
                    return toExtendedDto(itemRequest, Objects.isNull(items) ? Collections.emptyList() : items);
                })
                .collect(Collectors.toList());

    }

    ItemRequest fromDto(ItemRequestDtoIn dto);
}
