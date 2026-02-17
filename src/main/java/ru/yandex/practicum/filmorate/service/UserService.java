package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserDbStorage userStorage;

    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userStorage.findAll();
    }

    public User create(User user) {
        log.info("Попытка создания нового пользователя: {}", user);
        return userStorage.create(user);
    }

    public User update(User updatedUser) {
        log.info("Попытка обновления пользователя с ID: {}", updatedUser.getId());
        return userStorage.update(updatedUser);
    }

    public String remove(Integer id) {
        log.info("Удаление пользователя с ID: {}", id);
        return userStorage.remove(id);
    }

    public User userById(Integer id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userStorage.userById(id);
    }

    public User validateUserExists(Integer id) {
        return userStorage.validateUserExists(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        log.info("Получение списка друзей для пользователя с ID: {}", userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        log.info("Поиск общих друзей для пользователей {} и {}", userId, otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}