package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.validateUserExists(userId);
        userStorage.validateUserExists(friendId);
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }
        friends.putIfAbsent(userId, new HashSet<>());
        friends.putIfAbsent(friendId, new HashSet<>());
        friends.get(userId).add(friendId);
        friends.get(friendId).add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userStorage.validateUserExists(userId);
        userStorage.validateUserExists(friendId);
        if (friends.containsKey(userId) && friends.get(userId).contains(friendId)) {
            friends.get(userId).remove(friendId);
            friends.get(friendId).remove(userId);
        }
    }

    public Collection<User> getCommonFriends(Integer userId1, Integer userId2) {
        userStorage.validateUserExists(userId1);
        userStorage.validateUserExists(userId2);
        Set<Integer> friendsOfFirst = friends.getOrDefault(userId1, Collections.emptySet());
        Set<Integer> friendsOfSecond = friends.getOrDefault(userId2, Collections.emptySet());
        Set<Integer> commonFriendIds = new HashSet<>(friendsOfFirst);
        commonFriendIds.retainAll(friendsOfSecond);
        return commonFriendIds.stream()
                .map(userStorage::userById)
                .collect(Collectors.toList());
    }

    public Collection<User> getFriends(Integer userId) {
        userStorage.validateUserExists(userId);
        Set<Integer> friendIds = friends.getOrDefault(userId, Collections.emptySet());
        return friendIds.stream()
                .map(userStorage::userById)
                .collect(Collectors.toList());
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public String remove(Integer id) {
        return userStorage.remove(id);
    }

    public User userById(Integer id) {
        return userStorage.userById(id);
    }

    public User validateUserExists(Integer id) {
        return userStorage.validateUserExists(id);
    }
}
