package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.util.CrudStorageInMemory;

import java.util.*;

@Repository
public class UserStorageInMemory extends CrudStorageInMemory<User> implements UserStorage {
    @Override
    protected User setEntityId(User entity, long id) {
        entity.setId(id);
        return entity;
    }

    @Override
    protected Long getEntityId(User entity) {
        return entity.getId();
    }

//    private final Map<Long, User> users = new HashMap<>();
//    private long nextId = 1;
//
//    @Override
//    public User add(User user) {
//        long id = nextId++;
//        user.setId(id);
//        users.put(id, user);
//        return user;
//    }
//
//    @Override
//    public boolean update(User user) {
//        return Objects.isNull(
//                users.putIfAbsent(user.getId(), user));
//    }
//
//    @Override
//    public boolean remove(User user) {
//        return Objects.nonNull(
//                users.remove(user.getId()));
//    }
//
//    @Override
//    public List<User> getAll() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public Optional<User> getById(long id) {
//        return Optional.ofNullable(users.get(id));
//    }
}
