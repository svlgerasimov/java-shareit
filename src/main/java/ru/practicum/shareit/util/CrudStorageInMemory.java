package ru.practicum.shareit.util;

import java.util.*;

public abstract class CrudStorageInMemory<T> implements CrudStorage<T> {

    private final Map<Long, T> entities = new HashMap<>();
    private long nextId = 1;

    @Override
    public T add(T entity) {
        long id = nextId++;
        entity = setEntityId(entity, id);
        entities.put(id, entity);
        return entity;
    }

    @Override
    public boolean update(T entity) {
        long id = getEntityId(entity);
        if (entities.containsKey(id)) {
            entities.put(id, entity);
            return true;
        }
        return false;
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

    abstract protected T setEntityId(T entity, long id);

    abstract protected Long getEntityId(T entity);
}
