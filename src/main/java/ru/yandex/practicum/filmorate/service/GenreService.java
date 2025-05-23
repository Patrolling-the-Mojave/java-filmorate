package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreStorage;

    public Genre findById(int id) {
        return genreStorage.findById(id).orElseThrow(() -> new NotFoundException("жанр с id " + id + " не найден"));
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll().stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
