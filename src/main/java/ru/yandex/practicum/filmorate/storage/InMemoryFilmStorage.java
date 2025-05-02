package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 1;

    @Override
    public Film findById(int id) {
        return films.get(id);
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
    public Film save(Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        return film;
    }

    private int getNewId() {
        if (!films.containsKey(globalId)) {
            return globalId;
        }
        globalId++;
        return getNewId();
    }
}
