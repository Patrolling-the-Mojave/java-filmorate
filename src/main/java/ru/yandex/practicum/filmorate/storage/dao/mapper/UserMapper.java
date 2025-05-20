package ru.yandex.practicum.filmorate.storage.dao.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

@RequiredArgsConstructor
public class UserMapper {
    public static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday"))
                .login(rs.getString("login"))
                .friends(new HashSet<>())
                .friendship(new HashMap<>())
                .build();
        return user;
    }

    public static User mapNewUserToUser(NewUserDto newUser) {
        return User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .login(newUser.getLogin())
                .birthday(newUser.getBirthday())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .login(user.getLogin())
                .friends(user.getFriends())
                .birthday(user.getBirthday())
                .build();
    }

}
