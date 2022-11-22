package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.Objects;

public class ItemMapper {

    private ItemMapper() {

    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item fromItemDto(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static void patchFromDto(Item itemToPatch, ItemDto patchDto) {
        String patchName = patchDto.getName();
        if (Objects.nonNull(patchName)) {
            itemToPatch.setName(patchName);
        }
        String patchDescription = patchDto.getDescription();
        if (Objects.nonNull(patchDescription)) {
            itemToPatch.setDescription(patchDescription);
        }
        Boolean patchAvailable = patchDto.getAvailable();
        if (Objects.nonNull(patchAvailable)) {
            itemToPatch.setAvailable(patchAvailable);
        }
    }
}
