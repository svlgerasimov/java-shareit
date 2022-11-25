package ru.practicum.shareit.util;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;

public abstract class CrudStorageInMemory<T> implements CrudStorage<T> {

    @Getter(AccessLevel.PROTECTED)
    public final Map<Long, T> entities = new HashMap<>();
    private long nextId = 1;

    @Override
    public T add(T entity) {
        long id = nextId++;
        entity = setEntityId(entity, id);
        entities.put(id, entity);
        return entity;
    }

    @Override
    public boolean remove(long id) {
        return Objects.nonNull(
                entities.remove(id));
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Optional<T> getById(long id) {
        return Optional.ofNullable(entities.get(id));
    }

    protected abstract T setEntityId(T entity, long id);

    protected abstract Long getEntityId(T entity);
}
