package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Попытка создания нового пользователя: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        log.info("Попытка обновления пользователя с ID: {}", updatedUser.getId());
        return userService.update(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void remove(@Valid @PathVariable("id") Integer id) {
        log.info("Удаление пользователя с ID: {}", id);
        userService.remove(id);
    }

    @GetMapping("/{id}")
    public User userById(@Valid @PathVariable("id") Integer id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userService.userById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @Valid @PathVariable("id") Integer userId,
            @Valid @PathVariable("friendId") Integer friendId) {
        log.info("Добавление пользователя {} в друзья пользователя {}", friendId, userId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @Valid @PathVariable("id") Integer userId,
            @Valid @PathVariable("friendId") Integer friendId) {
        log.info("Удаление пользователя {} из друзей пользователя {}", friendId, userId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(
            @Valid @PathVariable("id") Integer userId) {
        log.info("Получение списка друзей пользователя с ID: {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @Valid @PathVariable("id") Integer userId,
            @Valid @PathVariable("otherId") Integer otherUserId) {
        log.info("Получение списка общих друзей пользователей {} и {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }
}
