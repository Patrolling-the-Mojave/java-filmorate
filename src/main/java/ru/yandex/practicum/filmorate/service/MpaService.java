package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaRatingDbStorage ratingDbStorage;

    public MpaRating findById(int id) {
        return ratingDbStorage.findById(id).orElseThrow(() -> new NotFoundException("рейтинг с id " + id + " не найден"));

    }

    public Collection<MpaRating> findAll() {
        return ratingDbStorage.findAll();
    }
}
