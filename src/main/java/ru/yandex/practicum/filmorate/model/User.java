package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @Email(message = "email должен соответствовать формату")
    @NotBlank(message = "email не должен быть пустым")
    private String email;

    @NotBlank(message = "логин не может быть пустым")
    private String login;
    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

}
