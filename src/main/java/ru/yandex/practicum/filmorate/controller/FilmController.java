package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OnCreate;
import ru.yandex.practicum.filmorate.model.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Validated(OnCreate.class) @RequestBody Film film) {
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.trace("фильм добавлен в коллекцию");
        log.debug("новый фильм - " + film);
        return film;
    }

    @PutMapping
    public Film update(@Validated(OnUpdate.class) @RequestBody Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("фильм с id " + newFilm.getId() + " не найден");
            throw new NotFoundException("фильм с id " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null) oldFilm.setName(newFilm.getName());
        if (newFilm.getDescription() != null) oldFilm.setDescription(newFilm.getDescription());
        if (newFilm.getReleaseDate() != null) oldFilm.setReleaseDate(newFilm.getReleaseDate());
        if (newFilm.getDuration() != null) oldFilm.setDuration(newFilm.getDuration());

        log.debug("обновленный фильм - " + oldFilm);
        return oldFilm;
    }

    private int getNewId() {
        if (!films.containsKey(globalId)) {
            return globalId;
        }
        globalId++;
        return getNewId();
    }
}
