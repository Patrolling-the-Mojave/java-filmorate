package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MpaRating {
    @NotNull
    private Integer id;
    @NotNull
    private String name;
}
