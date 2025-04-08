package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int globalId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        try {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("фильм не имеет названия");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("описание фильма не должно превышать 200 символов");
            }
            if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
                throw new ValidationException("фильм не мог выйти раньше " + CINEMA_BIRTHDAY);
            }
            if (film.getDuration() < 0) {
                throw new ValidationException("продолжительность фильма должна быть неотрицательна");
            }
        } catch (ValidationException ex) {
            log.warn("ошибка валидации фильма", ex);
            throw ex;
        }
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.trace("фильм добавлен в коллекцию");
        log.debug("новый фильм - " + film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        try {
            if (oldFilm == null) {
                throw new NotFoundException("фильм с id " + newFilm.getId() + " не найден");
            }
            if (newFilm.getName() != null) {
                if (newFilm.getName().isBlank()) {
                    throw new ValidationException("фильм должен иметь название");
                }
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                if (newFilm.getDescription().length() > 200) {
                    throw new ValidationException("описание фильма не должно превышать 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
                    throw new ValidationException("фильм не мог выйти раньше " + CINEMA_BIRTHDAY);
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDescription() != null) {
                if (newFilm.getDuration() < 0) {
                    throw new ValidationException("продолжительность фильма должна быть неотрицательна");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
        } catch (ValidationException ex) {
            log.warn("ошибка валидации фильма", ex);
            throw ex;
        } catch (NotFoundException ex) {
            log.warn("фильм не найден", ex);
            throw ex;
        }
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
