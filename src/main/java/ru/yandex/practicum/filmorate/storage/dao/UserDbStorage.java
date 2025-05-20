package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
@Primary
public class UserDbStorage implements UserStorage, FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void delete(int id) {
        final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id=?";
        jdbcTemplate.update(DELETE_BY_ID_QUERY, id);
        log.debug("пользователь с id {} удален", id);
    }

    @Override
    public Collection<User> findAll() {
        final String FIND_ALL_QUERY = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(FIND_ALL_QUERY, UserMapper::mapRow);
        users.forEach(this::loadFriendships);
        log.debug("поиск всех пользователей {}", users);
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id=?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, UserMapper::mapRow, id);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
        loadFriendships(user);
        return Optional.of(user);
    }

    @Override
    public User save(NewUserDto newUser) {
        final String INSERT_USER_QUERY = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connect -> {
            PreparedStatement ps = connect.prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getEmail());
            ps.setString(3, newUser.getLogin());
            ps.setDate(4, newUser.getBirthday());
            return ps;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        User user = UserMapper.mapNewUserToUser(newUser);
        user.setId(id);
        log.trace("сохранение пользователя {}", user);
        return user;
    }

    @Override
    public void update(User newUser) {
        final String UPDATE_USER_QUERY = "UPDATE users SET name =?, email=?, login=?, birthday=? WHERE id=?";
        int updatedRows = jdbcTemplate.update(UPDATE_USER_QUERY, newUser.getName(), newUser.getEmail(), newUser.getLogin(), newUser.getBirthday(), newUser.getId());

        if (updatedRows == 0) {
            throw new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден");
        }
    }

    private void loadFriendships(User user) {
        final String findFriendsQuery = "SELECT fp.friend_id, fps.name" +
                " FROM friendship AS fp" +
                " JOIN friendship_status AS fps ON fp.status_id = fps.id" +
                " WHERE fp.user_id=?";

        Map<Integer, FriendshipStatus> friendshipStatus = new HashMap<>();

        jdbcTemplate.query(findFriendsQuery, rs -> {
            int friendId = rs.getInt("friend_id");
            FriendshipStatus status = FriendshipStatus.mapFromDbValue(rs.getString("name"));
            friendshipStatus.put(friendId, status);
        }, user.getId());

        Set<User> friends = friendshipStatus.keySet().stream()
                .filter(key -> friendshipStatus.get(key).equals(FriendshipStatus.CONFIRMED))
                .map(id -> findById(id).get())
                .collect(Collectors.toSet());

        user.setFriends(friends);
        user.setFriendship(friendshipStatus);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        final String ADD_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, 1)";
        jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
    }
}
