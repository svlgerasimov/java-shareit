package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.CrudStorage;

import java.util.Optional;

public interface UserStorage extends CrudStorage<User> {
    Optional<User> getByEmail(String email);

    Optional<User> getByEmailExcludeId(String email, long id);
}
