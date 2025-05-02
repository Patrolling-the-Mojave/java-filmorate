package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Film save(Film film);

    public void delete(int id);

    public Film findById(int id);

    public Collection<Film> findAll();
}
