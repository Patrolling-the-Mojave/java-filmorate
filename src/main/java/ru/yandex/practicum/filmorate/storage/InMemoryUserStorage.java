package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int globalId = 1;

    @Override
    public User save(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(int id) {
        return users.get(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    private int getNewId() {
        if (!users.containsKey(globalId)) {
            return globalId;
        }
        globalId++;
        return getNewId();
    }

}
