package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import ru.yandex.practicum.filmorate.annotation.Login;

import java.sql.Date;
import java.util.Set;

@Data
@Builder
@Setter
public class User {
    private Integer id;
    private Set<Friendship> friends;
    @Email(message = "email должен соответствовать формату")
    @NotBlank(message = "email не должен быть пустым")
    private String email;

    @NotBlank(message = "логин не может быть пустым")
    @Login
    private String login;
    @Size(max = 15)
    private String name;

    @Past(message = "дата рождения не может быть в будущем")
    private Date birthday;
}
