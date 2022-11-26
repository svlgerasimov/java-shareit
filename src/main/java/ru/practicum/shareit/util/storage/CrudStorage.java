package ru.practicum.shareit.util.storage;

import java.util.List;
import java.util.Optional;

public interface CrudStorage<T> {

    T add(T entity);

    boolean remove(long id);

    List<T> getAll();

    Optional<T> getById(long id);
}
