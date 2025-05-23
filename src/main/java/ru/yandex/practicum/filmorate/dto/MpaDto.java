package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MpaDto {
    private final Integer id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;
}
