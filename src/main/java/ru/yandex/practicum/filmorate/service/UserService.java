package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NoContentException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.Updates.runIfNotNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public UserDto addUser(NewUserDto newUser) {
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
            log.trace("имя пользователя задано его логином");
        }
        log.trace("пользователь добавлен в коллекцию");
        log.debug("новый пользователь - " + newUser);
        User user = userStorage.save(newUser);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserDto newUser) {
        Optional<User> oldUserOpt = userStorage.findById(newUser.getId());
        User oldUser = oldUserOpt.orElseThrow(() -> new NotFoundException("Пользователь с id=" + newUser.getId() + " не найден"));
        log.debug("oldUser: " + oldUser);
        runIfNotNull(newUser.getEmail(), () -> oldUser.setEmail(newUser.getEmail()));
        runIfNotNull(newUser.getBirthday(), () -> oldUser.setBirthday(newUser.getBirthday()));
        runIfNotNull(newUser.getLogin(), () -> oldUser.setLogin(newUser.getLogin()));
        runIfNotNull(newUser.getName(), () -> oldUser.setName(newUser.getName()));
        log.debug("обновленный пользователь - " + oldUser);
        userStorage.update(oldUser);
        return UserMapper.mapToUserDto(oldUser);
    }

    public Collection<UserDto> findAll() {
        log.debug("запрос всех пользователей");
        return userStorage.findAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto findById(int id) {
        Optional<User> userOpt = userStorage.findById(id);
        log.trace("запрос пользователя с Id " + id);
        return UserMapper.mapToUserDto(userOpt.orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден")));
    }

    public List<UserDto> addFriend(int id, int friendId) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));
        user.getFriends().add(friend);
        log.trace("друг " + friend + " добавлен");
        log.debug("обновленный список друзей " + user.getFriends());
        friendStorage.addFriend(id, friendId);
        return List.of(UserMapper.mapToUserDto(user), UserMapper.mapToUserDto(friend));
    }

    public UserDto deleteFriend(int id, int friendId) {
        Optional<User> userOpt = userStorage.findById(id);
        Optional<User> friendOpt = userStorage.findById(friendId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        if (friendOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id - " + friendId + " не найден");
        }
        User user = userOpt.get();
        User friend = friendOpt.get();
        if (!user.getFriends().contains(friend)) {
            throw new NoContentException(String.format("пользователь %d не является другом для %d", friendId, id));
        }
        user.getFriends().remove(friend);
        log.trace("друг " + friend + " удален");
        log.debug("обновленный список друзей " + user.getFriends());
        friendStorage.deleteFriend(id, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getFriendsByUserId(int id) {
        Optional<User> userOpt = userStorage.findById(id);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        User user = userOpt.get();
        log.debug("запрос друзей пользователя " + id);
        return userStorage.findAll().stream()
                .filter(usr -> user.getFriends().contains(usr))
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getCommonFiends(int id, int otherId) {
        Optional<User> userOpt = userStorage.findById(id);
        Optional<User> otherOpt = userStorage.findById(otherId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        if (otherOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id - " + otherId + " не найден");
        }
        User user = userOpt.get();
        User other = otherOpt.get();
        log.debug("запрос списка общих для " + id + " и " + otherId + " друзей");
        return user.getFriends().stream()
                .filter(friend -> other.getFriends().contains(friend))
                .map(UserMapper::mapToUserDto)
                .toList();

    }

}
