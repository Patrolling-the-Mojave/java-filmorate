package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoContentException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.Updates.runIfNotNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.trace("имя пользователя задано его логином");
        }
        log.trace("пользователь добавлен в коллекцию");
        log.debug("новый пользователь - " + user);
        return userStorage.save(user);
    }

    public User updateUser(User newUser) {
        User oldUser = userStorage.findById(newUser.getId());
        log.debug("oldUser: " + oldUser);
        if (oldUser == null) {
            throw new NotFoundException("пользователь с id - " + newUser.getId() + " не найден");
        }
        System.out.println(oldUser);
        runIfNotNull(newUser.getEmail(), () -> oldUser.setEmail(newUser.getEmail()));
        runIfNotNull(newUser.getBirthday(), () -> oldUser.setBirthday(newUser.getBirthday()));
        runIfNotNull(newUser.getLogin(), () -> oldUser.setLogin(newUser.getLogin()));
        runIfNotNull(newUser.getName(), () -> oldUser.setName(newUser.getName()));
        log.debug("обновленный пользователь - " + oldUser);
        return oldUser;
    }

    public Collection<User> findAll() {
        log.debug("запрос всех пользователей");
        return userStorage.findAll();
    }

    public User findById(int id) {
        if (userStorage.findById(id) == null) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        log.trace("запрос пользователя с Id " + id);
        return userStorage.findById(id);
    }

    public List<User> addFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);
        if (user == null) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("пользователь с id - " + friendId + " не найден");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.trace("друг " + friend + " добавлен");
        log.debug("обновленный список друзей " + user.getFriends());
        return List.of(user, friend);
    }

    public List<User> deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);
        if (user == null) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("пользователь с id - " + friendId + " не найден");
        }
        if (!user.getFriends().contains(friendId)) {
            throw new NoContentException(String.format("пользователь %d не является другом для %d", friendId, id));
        }
        if (!friend.getFriends().contains(id)) {
            throw new NoContentException(String.format("пользователь %d не является другом для %d", id, friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.trace("друг " + friend + " удален");
        log.debug("обновленный список друзей " + user.getFriends());
        return List.of(user, friend);
    }

    public List<User> getFriendsByUserId(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        log.debug("запрос друзей пользователя " + id);
        return userStorage.findAll().stream()
                .filter(usr -> user.getFriends().contains(usr.getId()))
                .toList();
    }

    public List<User> getCommonFiends(int id, int otherId) {
        User user = userStorage.findById(id);
        User other = userStorage.findById(otherId);
        if (user == null) {
            throw new NotFoundException("пользователь с id - " + id + " не найден");
        }
        if (other == null) {
            throw new NotFoundException("пользователь с id - " + otherId + " не найден");
        }
        log.debug("запрос списка общих для " + id + " и " + otherId + " друзей");
        return user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .map(userStorage::findById)
                .toList();

    }

}
