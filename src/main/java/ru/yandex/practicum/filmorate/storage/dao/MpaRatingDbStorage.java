package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MpaRatingDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<MpaRating> findById(int id) {
        final String FIND_RATING_BY_ID = "SELECT name FROM mpa_rating WHERE id=?";
        MpaRating mpaRating;
        try {
            mpaRating = jdbcTemplate.queryForObject(FIND_RATING_BY_ID, (rs, rowNum) ->
                    new MpaRating(id, rs.getString("name")), id);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
        return Optional.ofNullable(mpaRating);
    }

    @Override
    public Collection<MpaRating> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa_rating", (rs, rowNum) ->
                new MpaRating(rs.getInt("id"), rs.getString("name")));
    }
}
