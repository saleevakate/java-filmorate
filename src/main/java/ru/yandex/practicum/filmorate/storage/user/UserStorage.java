package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User user);

    String remove(Integer id);

    User userById(Integer id);

    Collection<User> findAll();

    User validateUserExists(Integer id);
}