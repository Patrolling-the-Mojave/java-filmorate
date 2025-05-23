package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDate;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class FilmDto {
    @NotNull
    private Integer id;
    private Set<Integer> likes = new HashSet<>();
    private Set<GenreDto> genres = new TreeSet<>(Comparator.comparingInt(GenreDto::getId));
    private MpaRating mpa;
    @NotBlank(message = "имя не должно быть пустым")
    private String name;
    @Size(max = 200, message = "превышен максимальный размер описания")
    private String description;

    @NotNull(message = "дата релиза обязательна для добавления")
    @FilmReleaseDate
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
