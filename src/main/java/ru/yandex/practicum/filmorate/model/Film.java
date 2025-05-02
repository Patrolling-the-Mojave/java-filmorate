package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmReleaseDate;
import ru.yandex.practicum.filmorate.annotation.OnCreate;
import ru.yandex.practicum.filmorate.annotation.OnUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film implements Comparable<Film> {
    @NotNull(groups = OnUpdate.class)
    private Integer id;
    private final Set<Integer> likes = new HashSet<>();

    @NotBlank(groups = OnCreate.class, message = "имя не должно быть пустым")
    private String name;
    @Size(max = 200, message = "превышен максимальный размер описания", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(groups = OnCreate.class, message = "дата релиза обязательна для добавления")
    @FilmReleaseDate(groups = {OnUpdate.class, OnCreate.class})
    private LocalDate releaseDate;

    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Integer duration;

    @Override
    public int compareTo(Film o) {
        return Integer.compare(this.likes.size(), o.likes.size());
    }
}
