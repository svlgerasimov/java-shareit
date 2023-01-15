package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPatchDtoMapper {

    @Mapping(target = "id", ignore = true)
    void updateWithPatchDto(@MappingTarget User user, UserPatchDto patchDto);
}
