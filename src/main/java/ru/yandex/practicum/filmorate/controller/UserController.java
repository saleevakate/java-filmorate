package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
            log.info("Попытка создания нового пользователя: {}", user);
            if (user.getName() == null) {
                user.setName(user.getLogin());
                log.debug("Имя пользователя не указано, установлено по логину: {}", user.getName());
            }
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан с ID: {}", user.getId());
            return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
            log.info("Попытка обновления пользователя с ID: {}", updatedUser.getId());
            User existingUser = users.get(updatedUser.getId());
            if (existingUser == null) {
                log.warn("Пользователь с ID {} не найден", updatedUser.getId());
                throw new ValidationException("Пользователь с указанным ID не найден");
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
                log.debug("Обновлён email пользователя ID {}: {}", updatedUser.getId(), updatedUser.getEmail());
            }
            if (updatedUser.getLogin() != null) {
                existingUser.setLogin(updatedUser.getLogin());
                log.debug("Обновлён логин пользователя ID {}: {}", updatedUser.getId(), updatedUser.getLogin());
            }
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
                log.debug("Обновлено имя пользователя ID {}: {}", updatedUser.getId(), updatedUser.getName());
            }
            if (updatedUser.getBirthday() != null) {
                existingUser.setBirthday(updatedUser.getBirthday());
                log.debug("Обновлена дата рождения пользователя ID {}: {}", updatedUser.getId(), updatedUser.getBirthday());
            }
            log.info("Пользователь ID {} успешно обновлен", updatedUser.getId());
            return existingUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        int nextId = ++currentMaxId;
        log.debug("Сгенерирован новый ID для пользователя: {}", nextId);
        return nextId;
    }
}
