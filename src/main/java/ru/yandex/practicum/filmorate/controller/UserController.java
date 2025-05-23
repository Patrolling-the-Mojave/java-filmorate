package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public UserDto create(@Validated @RequestBody NewUserDto user) {
        return userService.addUser(user);
    }

    @PutMapping
    public UserDto update(@Validated @RequestBody UpdateUserDto newUser) {
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable int id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<UserDto> addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto removeFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@PathVariable int id) {
        return userService.getFriendsByUserId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFiends(id, otherId);
    }
}

