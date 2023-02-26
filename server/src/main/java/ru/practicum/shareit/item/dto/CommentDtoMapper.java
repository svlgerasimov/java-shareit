package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDtoOut toDto(Comment comment);

    List<CommentDtoOut> toDto(List<Comment> comments);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment fromDto(CommentDtoIn dto);
}
