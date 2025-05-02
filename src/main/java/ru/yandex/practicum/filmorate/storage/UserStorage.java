package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User save(User user);

    public void delete(int id);

    public User findById(int id);

    public Collection<User> findAll();

}
