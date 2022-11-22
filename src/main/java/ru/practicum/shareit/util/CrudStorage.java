package ru.practicum.shareit.util;

import java.util.List;
import java.util.Optional;

public interface CrudStorage<T> {
    T add(T entity);
    boolean update(T entity);
    boolean remove(long id);
    List<T> getAll();
    Optional<T> getById(long id);
}
