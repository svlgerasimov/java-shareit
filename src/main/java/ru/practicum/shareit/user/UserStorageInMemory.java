package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.util.CrudStorageInMemory;

import java.util.*;

@Repository
public class UserStorageInMemory extends CrudStorageInMemory<User> implements UserStorage {
    @Override
    public Optional<User> getByEmail(String email) {
        // да, медленно, но in-memory это же временное решение
        return super.getEntities().values().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findAny();
    }

    @Override
    public Optional<User> getByEmailExcludeId(String email, long id) {
        return super.getEntities().values().stream()
                .filter(user ->
                        Objects.equals(user.getEmail(), email) && !Objects.equals(user.getId(), id))
                .findAny();
    }

    @Override
    protected User setEntityId(User entity, long id) {
        entity.setId(id);
        return entity;
    }

    @Override
    protected Long getEntityId(User entity) {
        return entity.getId();
    }

}
