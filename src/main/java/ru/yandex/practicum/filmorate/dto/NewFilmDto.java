package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class NewFilmDto {
    @NotBlank(message = "имя не должно быть пустым")
    private String name;
    @Size(max = 200, message = "превышен максимальный размер описания")
    private String description;
    @NotNull(message = "дата релиза обязательна для добавления")
    @FilmReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма не может быть отрицательна")
    private Integer duration;
    private MpaDto mpa;
    private Set<GenreDto> genres;
}
