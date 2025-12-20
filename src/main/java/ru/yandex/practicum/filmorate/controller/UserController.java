package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            log.info("Попытка создания нового пользователя: {}", user);
            validateUser(user);
            if (user.getName() == null) {
                user.setName(user.getLogin());
                log.debug("Имя пользователя не указано, установлено по логину: {}", user.getName());
            }
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан с ID: {}", user.getId());
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка валидации при создании пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        try {
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
        } catch (ValidationException e) {
            log.error("Ошибка валидации при обновлении пользователя: {}", e.getMessage());
            throw e;
        }
    }

    private void validateUser(User user) {
        log.debug("Начало валидации пользователя: {}", user);
        if (user == null) {
            log.warn("Пользователь равен null");
            throw new ValidationException("Пользователь не может быть null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Email пользователя пуст или null");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Email пользователя не содержит символ @: {}", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Логин пользователя пуст или null");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин пользователя содержит пробелы: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() == null) {
            log.warn("Дата рождения пользователя равна null");
            throw new ValidationException("Дата рождения не может быть null");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        log.debug("Валидация пользователя успешно пройдена: {}", user);
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        int nextId = ++currentMaxId;
        log.debug("Сгенерирован новый ID для пользователя: {}", nextId);
        return nextId;
    }
}
