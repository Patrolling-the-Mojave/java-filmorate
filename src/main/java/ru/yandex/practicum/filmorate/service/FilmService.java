package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;

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
    private final LikeStorage likeStorage;

    public FilmDto addFilm(NewFilmDto newFilm) {
        log.debug("новый фильм - " + newFilm);
        Film film = filmStorage.save(newFilm);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmDto newFilm) {
        Film oldFilm = filmStorage.findById(newFilm.getId()).orElseThrow(() -> new NotFoundException("фильм с id " + newFilm.getId() + " не найден"));
        runIfNotNull(newFilm.getName(), () -> oldFilm.setName(newFilm.getName()));
        runIfNotNull(newFilm.getDescription(), () -> oldFilm.setDescription(newFilm.getDescription()));
        runIfNotNull(newFilm.getReleaseDate(), () -> oldFilm.setReleaseDate(newFilm.getReleaseDate()));
        runIfNotNull(newFilm.getDuration(), () -> oldFilm.setDuration(newFilm.getDuration()));
        runIfNotNull(newFilm.getMpa(), () -> oldFilm.setMpa(newFilm.getMpa()));
        runIfNotNull(newFilm.getGenres(), () -> oldFilm.setGenres(newFilm.getGenres()));
        runIfNotNull(newFilm.getLikes(), () -> oldFilm.setLikes(newFilm.getLikes()));
        log.debug("обновленный фильм - " + oldFilm);
        filmStorage.update(oldFilm);
        return FilmMapper.mapToFilmDto(oldFilm);
    }

    public Collection<FilmDto> findAll() {
        log.info("получение всех фильмов");
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto findById(int id) {
        Film film = filmStorage.findById(id).orElseThrow(() -> new NotFoundException("фильм с id " + id + " не найден"));
        return FilmMapper.mapToFilmDto(film);
    }

    public Set<Integer> addLike(int id, int userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("пользователь с id " + id + " не найден");
        }
        Film film = filmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("фильм с id " + id + " не найден"));
        film.getLikes().add(userId);
        likeStorage.addLike(id, userId);
        return film.getLikes();
    }

    public Set<Integer> removeLike(int id, int userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("пользователь с id " + id + " не найден");
        }
        Film film = filmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("фильм с id " + id + " не найден"));
        film.getLikes().remove(userId);
        likeStorage.removeLike(id, userId);
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
