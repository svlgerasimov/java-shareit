package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.storage.CrudStorageInMemory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemory extends CrudStorageInMemory<Item> implements ItemStorage {
    @Override
    protected Item setEntityId(Item entity, long id) {
        entity.setId(id);
        return entity;
    }

    @Override
    protected Long getEntityId(Item entity) {
        return entity.getId();
    }

    @Override
    public List<Item> getAll(User owner) {
        return getEntities().values().stream()
                .filter(item -> Objects.equals(item.getOwner(), owner))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String lowerCaseText = text.toLowerCase();
        return getEntities().values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText))
                                && item.getAvailable())
                .collect(Collectors.toList());
    }
}
