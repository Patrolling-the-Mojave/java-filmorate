package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.Updates.runIfNotNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        log.trace("фильм добавлен в коллекцию");
        log.debug("новый фильм - " + film);
        return filmStorage.save(film);
    }

    public Film updateFilm(Film newFilm) {
        Film oldFilm = filmStorage.findById(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("фильм с id " + newFilm.getId() + " не найден");
        }
        runIfNotNull(newFilm.getName(), () -> oldFilm.setName(newFilm.getName()));
        runIfNotNull(newFilm.getDescription(), () -> oldFilm.setDescription(newFilm.getDescription()));
        runIfNotNull(newFilm.getReleaseDate(), () -> oldFilm.setReleaseDate(newFilm.getReleaseDate()));
        runIfNotNull(newFilm.getDuration(), () -> oldFilm.setDuration(newFilm.getDuration()));
        log.debug("обновленный фильм - " + oldFilm);
        return oldFilm;
    }

    public Collection<Film> findAll() {
        log.info("получение всех фильмов");
        return filmStorage.findAll();
    }

    public Set<Integer> addLike(int id, int userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("пользователь с id - " + userId + " не найден");
        }
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("фильм с id " + id + " не найден");
        }
        film.getLikes().add(userId);
        return film.getLikes();
    }

    public Set<Integer> removeLike(int id, int userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("пользователь с id - " + userId + " не найден");
        }
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("фильм с id " + id + " не найден");
        }
        film.getLikes().remove(userId);
        return film.getLikes();
    }

    public List<Film> getMostPopularFilms(int count) {
        if (count <= 0) {
            throw new ParameterNotValidException("параметр count не должен быть отрицательным", count);
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

    }
}
