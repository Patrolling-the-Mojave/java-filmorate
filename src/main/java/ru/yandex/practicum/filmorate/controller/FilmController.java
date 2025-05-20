package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable int id) {
        return filmService.findById(id);
    }

    @PostMapping
    public FilmDto create(@Validated @RequestBody NewFilmDto film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto update(@Validated @RequestBody UpdateFilmDto newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Set<Integer> addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Set<Integer> deleteLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }
}
