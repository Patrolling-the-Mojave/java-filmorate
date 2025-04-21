package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Login;
import ru.yandex.practicum.filmorate.annotation.OnCreate;
import ru.yandex.practicum.filmorate.annotation.OnUpdate;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;

    @Email(message = "email должен соответствовать формату", groups = {OnUpdate.class, OnCreate.class})
    @NotBlank(message = "email не должен быть пустым", groups = OnCreate.class)
    private String email;

    @NotBlank(message = "логин не может быть пустым", groups = OnCreate.class)
    @Login(groups = {OnUpdate.class, OnCreate.class})
    private String login;
    @Size(max = 15)
    private String name;

    @Past(message = "дата рождения не может быть в будущем", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;

}
