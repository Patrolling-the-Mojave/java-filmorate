package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Login;

import java.sql.Date;

@Data
public class UpdateUserDto {
    @NotNull
    Integer id;
    @Size(max = 40)
    private String name;

    @Email(message = "email должен соответствовать формату")
    @NotBlank(message = "email не должен быть пустым")
    private String email;
    @Login
    @NotBlank
    private String login;
    @Past(message = "дата рождения не может быть в будущем")
    private Date birthday;
}
