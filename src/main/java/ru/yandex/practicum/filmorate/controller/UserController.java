package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.OnCreate;
import ru.yandex.practicum.filmorate.model.OnUpdate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int globalId = 1;


    @GetMapping
    public Collection<User> findAll() {
        log.info("получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Validated(OnCreate.class) @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.trace("имя пользователя задано его логином");
        }
        user.setId(getNewId());
        users.put(user.getId(), user);
        log.trace("пользователь добавлен в коллекцию");
        log.debug("новый пользователь - " + user);
        return user;
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User newUser) {
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("пользователь с id - " + newUser.getId() + " не найден");
            throw new NotFoundException("пользователь с id - " + newUser.getId() + " не найден");
        }
        if (newUser.getEmail() != null) oldUser.setEmail(newUser.getEmail());
        if (newUser.getBirthday() != null) oldUser.setBirthday(newUser.getBirthday());
        if (newUser.getLogin() != null) oldUser.setLogin(newUser.getLogin());
        if (newUser.getName() != null) oldUser.setName(newUser.getName());
        log.debug("обновленный пользователь - " + oldUser);
        return oldUser;
    }

    private int getNewId() {
        if (!users.containsKey(globalId)) {
            return globalId;
        }
        globalId++;
        return getNewId();
    }


}

