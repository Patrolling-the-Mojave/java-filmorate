package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int globalId = 1;

    @Override
    public User save(NewUserDto newUser) {
        User user = UserMapper.mapNewUserToUser(newUser);
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void update(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
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
