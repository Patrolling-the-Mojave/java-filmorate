package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User create(@Valid @RequestBody User user) {
        try {
            if (user.getLogin().contains(" ")) {
                throw new ValidationException("логин обязателен для регистрации и не должен содержать пробелов");
            }
        } catch (ValidationException ex) {
            log.warn("ошибка валидации пользователя", ex);
            throw ex;
        }
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
    public User update(@RequestBody User newUser) {
        User oldUser = users.get(newUser.getId());
        try {
            if (oldUser == null) {
                throw new NotFoundException("пользователь с id - " + newUser.getId() + " не найден");
            }
            if (newUser.getEmail() != null) {
                if (newUser.getEmail().isBlank()) {
                    throw new ValidationException("значение email отсутствует");
                }
                if (!newUser.getEmail().contains("@")) {
                    throw new ValidationException("email должен содержать символ - @");
                }
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null) {
                if (newUser.getLogin().contains(" ")) {
                    throw new ValidationException("логин обязателен для регистрации и не должен содержать пробелов");
                }
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                if (newUser.getBirthday().isAfter(LocalDate.now())) {
                    throw new ValidationException("невозможная дата рождения");
                }
                oldUser.setBirthday(newUser.getBirthday());
            }
        } catch (NotFoundException ex) {
            log.warn("пользователь не найден", ex);
            throw ex;
        } catch (ValidationException ex) {
            log.warn("ошибка валидации пользователя", ex);
            throw ex;
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
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

