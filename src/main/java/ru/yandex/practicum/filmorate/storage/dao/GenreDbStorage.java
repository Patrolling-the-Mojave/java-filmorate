package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(int id) {
        final String FIND_GENRE_BY_ID = "SELECT name FROM genre WHERE id=?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(FIND_GENRE_BY_ID, (rs, rowNum) -> new Genre(id, rs.getString("name")), id);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
        return Optional.ofNullable(genre);
    }

    @Override
    public Collection<Genre> findAll() {
        final String FIND_ALL_GENRES = "SELECT * FROM genre";
        return jdbcTemplate.query(FIND_ALL_GENRES, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")));
    }
}
