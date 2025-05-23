package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exception.NoContentException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        User oldUser = getUserById(newUser.getId());
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
        log.trace("запрос пользователя с Id " + id);
        return UserMapper.mapToUserDto(getUserById(id));
    }

    public List<UserDto> addFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(new Friendship(friend, FriendshipStatus.CONFIRMED));
        log.trace("друг " + friend + " добавлен");
        log.debug("обновленный список друзей " + user.getFriends());
        friendStorage.addFriend(id, friendId);
        return List.of(UserMapper.mapToUserDto(user), UserMapper.mapToUserDto(friend));
    }

    public UserDto deleteFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        Friendship friendship = new Friendship(friend, FriendshipStatus.CONFIRMED);
        if (!user.getFriends().contains(friendship)) {
            throw new NoContentException(String.format("пользователь %d не является другом для %d", friendId, id));
        }
        user.getFriends().remove(friendship);
        log.trace("друг " + friend + " удален");
        log.debug("обновленный список друзей " + user.getFriends());
        friendStorage.deleteFriend(id, friendId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getFriendsByUserId(int id) {
        User user = getUserById(id);
        Set<User> friends = user.getFriends().stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toSet());
        log.debug("запрос друзей пользователя " + id);

        return userStorage.findAll().stream()
                .filter(friends::contains)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getCommonFiends(int id, int otherId) {
        User user = getUserById(id);
        User other = getUserById(otherId);
        log.debug("запрос списка общих для " + id + " и " + otherId + " друзей");
        Set<User> userFriends = user.getFriends().stream().map(Friendship::getFriend).collect(Collectors.toSet());
        Set<User> otherFriends = other.getFriends().stream().map(Friendship::getFriend).collect(Collectors.toSet());

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private User getUserById(int id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("пользователь с id - " + id + " не найден"));
    }

}
