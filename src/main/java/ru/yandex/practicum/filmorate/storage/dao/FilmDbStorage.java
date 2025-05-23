package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage, LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;


    @Override
    public void update(Film film) {
        final String UPDATE_FILM_QUERY = "UPDATE film SET name=?, description=?, release_date=?, rating_id=?, duration=? WHERE id=?";
        jdbcTemplate.update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getDuration(),
                film.getId());
        if (film.getGenres() != null) {
            addUpdatedGenresToMovie(film.getId(), film.getGenres());
        }
    }

    private void addGenresToMovie(int filmId, Set<Genre> genres) {
        final String INSERT_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(INSERT_GENRE, genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList()));

    }

    private void addUpdatedGenresToMovie(int filmId, Set<Genre> genres) {
        final String DELETE_OLD_GENRES = "DELETE FROM film_genres WHERE film_id=?";
        jdbcTemplate.update(DELETE_OLD_GENRES, filmId);
        addGenresToMovie(filmId, genres);
    }

    @Override
    public Film save(NewFilmDto newFilm) {
        final String INSERT_NEW_FILM_QUERY = "INSERT INTO film (name, description, release_date, duration, rating_id)" +
                " VALUES(?, ?, ?, ?, ?)";
        Film film = FilmMapper.mapNewFilmToFilm(newFilm);
        if (newFilm.getMpa() != null) {
            film.setMpa(mpaStorage.findById(newFilm.getMpa().getId()).orElseThrow(() ->
                    new NotFoundException("mpa с id " + newFilm.getMpa().getId() + " не найден")));
        }
        if (newFilm.getGenres() != null) {
            film.setGenres(newFilm.getGenres().stream()
                    .map(genreDto -> genreStorage.findById(genreDto.getId()).orElseThrow((() -> new NotFoundException("жанр с id " + genreDto.getId() + " не найден"))))
                    .collect(Collectors.toSet()));
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_NEW_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(4, newFilm.getDuration());
            ps.setObject(5, (newFilm.getMpa() == null ? null : newFilm.getMpa().getId()));
            return ps;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToMovie(filmId, film.getGenres());
        }
        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        final String FIND_BY_ID_QUERY = "SELECT f.*, mpa.name AS mpa_name FROM film AS f" +
                " JOIN mpa_rating AS mpa ON f.rating_id = mpa.id" +
                " WHERE f.id=?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, FilmMapper::mapRow, id);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
        loadLikesAndGenres(List.of(film));
        return Optional.of(film);
    }

    @Override
    public Collection<Film> findAll() {
        final String FIND_ALL_QUERY = "SELECT f.*, mpa.name AS mpa_name FROM film AS f" +
                " JOIN mpa_rating AS mpa ON f.rating_id = mpa.id";
        List<Film> films = jdbcTemplate.query(FIND_ALL_QUERY, FilmMapper::mapRow);
        loadLikesAndGenres(films);
        return films;
    }

    private void loadLikesAndGenres(List<Film> films) {
        Set<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        Map<Integer, Set<Genre>> genresByFilmId = findGenresByFilmId(filmIds);
        Map<Integer, Set<Integer>> likesByFilmId = findLikesByFilmId(filmIds);
        for (Film film : films) {
            film.setGenres(genresByFilmId.getOrDefault(film.getId(), new HashSet<>()));
            film.setLikes(likesByFilmId.getOrDefault(film.getId(), new HashSet<>()));
        }
    }

    private Map<Integer, Set<Genre>> findGenresByFilmId(Set<Integer> filmIds) {
        String placeholder = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        final String FIND_GENRES = "SELECT fg.film_id, g.id, g.name FROM film_genres fg" +
                " JOIN genre AS g ON fg.genre_id = g.id" +
                " WHERE fg.film_id IN (" + placeholder + ")";
        Map<Integer, Set<Genre>> genresByFilmId = new HashMap<>();
        jdbcTemplate.query(FIND_GENRES,
                rs -> {
                    int filmId = rs.getInt("film_id");
                    Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));
                    genresByFilmId.computeIfAbsent(filmId, key -> new HashSet<>()).add(genre);
                }, filmIds.toArray());
        return genresByFilmId;
    }

    private Map<Integer, Set<Integer>> findLikesByFilmId(Set<Integer> filmIds) {
        String placeholder = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        final String FIND_LIKES = "SELECT film_id, user_id FROM film_likes WHERE film_id IN (" + placeholder + ")";
        Map<Integer, Set<Integer>> likesByFilmId = new HashMap<>();
        jdbcTemplate.query(FIND_LIKES,
                rs -> {
                    int filmId = rs.getInt("film_id");
                    int userId = rs.getInt("user_id");
                    likesByFilmId.computeIfAbsent(filmId, key -> new HashSet<>()).add(userId);
                }, filmIds.toArray());
        return likesByFilmId;
    }

    @Override
    public void addLike(int filmId, int userId) {
        final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(DELETE_LIKE_QUERY, filmId, userId);
    }


    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM film WHERE id =?", id);
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        return new HashSet<>(jdbcTemplate.queryForList("SELECT user_id FROM film_likes WHERE film_id=?", Integer.class, filmId));
    }
}
