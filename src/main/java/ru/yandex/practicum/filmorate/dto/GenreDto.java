package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreDto {
    private Integer id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;
}
