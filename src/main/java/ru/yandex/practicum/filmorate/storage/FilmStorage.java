package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Film save(NewFilmDto film);

    public void delete(int id);

    public Optional<Film> findById(int id);

    public Collection<Film> findAll();

    public void update(Film film);

}
