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

@Data
@Builder
public class Film {
    private Integer id;

    @NotBlank(groups = OnCreate.class, message = "имя не должно быть пустым")
    private String name;
    @Size(max = 200, message = "превышен максимальный размер описания", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(groups = OnCreate.class, message = "дата релиза обязательна для добавления")
    @FilmReleaseDate(groups = {OnUpdate.class, OnCreate.class})
    private LocalDate releaseDate;

    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Integer duration;
}
