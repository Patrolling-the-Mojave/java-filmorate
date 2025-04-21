package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.OnCreate;
import ru.yandex.practicum.filmorate.annotation.OnUpdate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Updates.runIfNotNull;

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
        runIfNotNull(newFilm.getName(), () -> oldFilm.setName(newFilm.getName()));
        runIfNotNull(newFilm.getDescription(), () -> oldFilm.setDescription(newFilm.getDescription()));
        runIfNotNull(newFilm.getReleaseDate(), () -> oldFilm.setReleaseDate(newFilm.getReleaseDate()));
        runIfNotNull(newFilm.getDuration(), () -> oldFilm.setDuration(newFilm.getDuration()));
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
