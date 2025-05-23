package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@Slf4j
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 1;

    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public void delete(int id) {
        films.remove(id);
    }

    @Override
    public Film save(NewFilmDto newFilm) {
        Film film = FilmMapper.mapNewFilmToFilm(newFilm);
        film.setId(getNewId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    private int getNewId() {
        if (!films.containsKey(globalId)) {
            return globalId;
        }
        globalId++;
        return getNewId();
    }
}
