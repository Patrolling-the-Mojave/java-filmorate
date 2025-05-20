package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UpdateFilmDto {
    @NotNull
    private Integer id;
    @Size(max = 20)
    private String name;
    private Set<Integer> likes;
    private Set<Genre> genres;
    @Size(max = 200, message = "превышен максимальный размер описания")
    private String description;
    @FilmReleaseDate
    private LocalDate releaseDate;
    private MpaRating mpa;
    @Positive(message = "продолжительность фильма не может быть отрицательна")
    private Integer duration;
}
