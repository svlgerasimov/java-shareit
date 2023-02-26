package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemPatchDtoMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateWithPatchDto(@MappingTarget Item item, ItemPatchDto patchDto);
}
