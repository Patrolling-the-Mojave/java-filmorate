package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @PositiveOrZero
    private int duration;
}
