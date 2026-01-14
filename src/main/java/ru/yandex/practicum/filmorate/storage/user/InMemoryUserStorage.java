package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, установлено по логину: {}", user.getName());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User updatedUser) {
        User existingUser = validateUserExists(updatedUser.getId()); //проверка наличия пользователя теперь здесь
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

    @Override
    public String remove(Integer id) {
        User user = validateUserExists(id);
        log.info("Удаления пользователя: {}", user);
        users.remove(id);
        return "Пользователь успешно удален";
    }

    @Override
    public User userById(Integer id) {
        return validateUserExists(id);
    }

    @Override
    public User validateUserExists(Integer id) {
        log.info("Проверка существования пользователя с ID: {}", id);
        User userId = users.get(id);
        if (userId == null) {
            throw new ValidationException("Пользователь с ID " + id + " не найден");
        }
        return userId;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получен список всех пользователей");
        return users.values();
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
