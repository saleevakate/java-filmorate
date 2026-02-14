package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    //методы добавления, удаления и изменения объектов
    User create(User user);

    User update(User user);

    String remove(Integer id);

    User userById(Integer id);

    public Collection<User> findAll();

    public User validateUserExists(Integer id);
}
