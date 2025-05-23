package ru.yandex.practicum.filmorate.storage.dao.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class FilmMapper {

    public static Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .name(rs.getString("name"))
                .id(rs.getInt("id"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .mpa(new MpaRating(rs.getInt("rating_id"), rs.getString("mpa_name")))
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();
    }

    public static Film mapNewFilmToFilm(NewFilmDto newFilm) {
        return Film.builder()
                .name(newFilm.getName())
                .releaseDate(newFilm.getReleaseDate())
                .duration(newFilm.getDuration())
                .description(newFilm.getDescription())
                .duration(newFilm.getDuration())
                .build();
    }

    public static Film mapUpdatedFilmToFilm(UpdateFilmDto updatedFilm) {
        return Film.builder()
                .id(updatedFilm.getId())
                .name(updatedFilm.getName())
                .mpa(updatedFilm.getMpa())
                .releaseDate(updatedFilm.getReleaseDate())
                .duration(updatedFilm.getDuration())
                .description(updatedFilm.getDescription())
                .duration(updatedFilm.getDuration())
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(film.getLikes());
        if (film.getGenres() != null) {
            film.getGenres().stream().map(GenreMapper::mapToGenreDto)
                    .forEach(dto.getGenres()::add);
        }
        if (film.getMpa() != null) {
            dto.setMpa(film.getMpa());
        }
        return dto;
    }
}
