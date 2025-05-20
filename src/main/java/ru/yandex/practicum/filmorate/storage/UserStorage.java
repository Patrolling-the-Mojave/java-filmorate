package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    public User save(NewUserDto user);

    public void delete(int id);

    public Optional<User> findById(int id);

    public Collection<User> findAll();

    public void update(User newUser);

}
