package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Integer id;
    private String name;
    private String email;
    private String login;
    private Date birthday;
    private Set<User> friends;
}
