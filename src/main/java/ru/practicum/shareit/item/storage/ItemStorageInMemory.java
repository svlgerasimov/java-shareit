package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.storage.CrudStorageInMemory;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageInMemory extends CrudStorageInMemory<Item> implements ItemStorage {

    private final Map<Long, List<Item>> userItemIndex = new HashMap<>();

    @Override
    public Item add(Item item) {
        item = super.add(item);
        userItemIndex
                .computeIfAbsent(item.getOwner().getId(), ownerId -> new ArrayList<>())
                .add(item);
        return item;
    }

    @Override
    public boolean remove(long id) {
        userItemIndex.values()
                .forEach(items -> items.removeIf(item -> item.getId().equals(id)));
        return super.remove(id);
    }

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
        return userItemIndex.getOrDefault(owner.getId(), Collections.emptyList());
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
