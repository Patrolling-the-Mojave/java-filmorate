package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.Login;

import java.sql.Date;

@Data
public class NewUserDto {
    @Size(max = 20)
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
