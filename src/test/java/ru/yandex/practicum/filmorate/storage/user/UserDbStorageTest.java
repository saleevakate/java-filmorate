package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class})
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@test.com", "user1", "User One", LocalDate.of(1990, 1, 1));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@test.com", "user2", "User Two", LocalDate.of(1991, 2, 2));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user3@test.com", "user3", "User Three", LocalDate.of(1992, 3, 3));
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        Collection<User> users = userStorage.findAll();
        assertThat(users).isNotNull();
        assertThat(users).hasSize(3);
    }

    @Test
    void userById_WithValidId_ShouldReturnUser() {
        User user = userStorage.userById(1);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("user1@test.com");
        assertThat(user.getLogin()).isEqualTo("user1");
        assertThat(user.getName()).isEqualTo("User One");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void userById_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userStorage.userById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void create_ShouldAddNewUser() {
        User newUser = new User();
        newUser.setEmail("newuser@test.com");
        newUser.setLogin("newuser");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        User createdUser = userStorage.create(newUser);
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(4);
        assertThat(createdUser.getEmail()).isEqualTo("newuser@test.com");
        User savedUser = userStorage.userById(4);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("New User");
    }

    @Test
    void create_WithBlankName_ShouldSetNameFromLogin() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("testlogin");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        User createdUser = userStorage.create(newUser);
        assertThat(createdUser.getName()).isEqualTo("testlogin");
    }

    @Test
    void create_WithNullName_ShouldSetNameFromLogin() {
        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setLogin("testlogin");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        User createdUser = userStorage.create(newUser);
        assertThat(createdUser.getName()).isEqualTo("testlogin");
    }

    @Test
    void create_WithDuplicateEmail_ShouldThrowException() {
        User newUser = new User();
        newUser.setEmail("user1@test.com");
        newUser.setLogin("newlogin");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        assertThatThrownBy(() -> userStorage.create(newUser))
                .isInstanceOf(Exception.class);
    }

    @Test
    void update_ShouldModifyExistingUser() {
        User user = userStorage.userById(1);
        user.setName("Updated Name");
        user.setEmail("updated@test.com");
        User updatedUser = userStorage.update(user);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
        User retrievedUser = userStorage.userById(1);
        assertThat(retrievedUser.getName()).isEqualTo("Updated Name");
        assertThat(retrievedUser.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    void update_WithInvalidId_ShouldThrowNotFoundException() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@test.com");
        user.setLogin("test");
        user.setName("Test");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        assertThatThrownBy(() -> userStorage.update(user))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void remove_ShouldDeleteUser() {
        userStorage.remove(1);
        assertThatThrownBy(() -> userStorage.userById(1))
                .isInstanceOf(NotFoundException.class);
        Collection<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void remove_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userStorage.remove(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void validateUserExists_WithValidId_ShouldReturnUser() {
        User user = userStorage.validateUserExists(1);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    void validateUserExists_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userStorage.validateUserExists(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void addFriend_ShouldAddFriend() {
        userStorage.addFriend(1, 2);

        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).isNotEmpty();
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(2);
    }

    @Test
    void addFriend_WithSameId_ShouldThrowException() {
        assertThatThrownBy(() -> userStorage.addFriend(1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Нельзя добавить себя в друзья");
    }

    @Test
    void addFriend_DuplicateFriend_ShouldNotCreateDuplicate() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 2);

        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).hasSize(1);
    }

    @Test
    void removeFriend_ShouldRemoveFriend() {
        userStorage.addFriend(1, 2);
        List<User> friendsBefore = userStorage.getFriends(1);
        assertThat(friendsBefore).hasSize(1);
        userStorage.removeFriend(1, 2);
        List<User> friendsAfter = userStorage.getFriends(1);
        assertThat(friendsAfter).isEmpty();
    }

    @Test
    void removeFriend_WithNonExistentFriend_ShouldThrowNotFoundException() {
        // При удалении друга с несуществующим ID должно выбрасываться исключение
        assertThatThrownBy(() -> userStorage.removeFriend(1, 999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void removeFriend_WithNonExistentUser_ShouldThrowNotFoundException() {
        // При удалении друга у несуществующего пользователя должно выбрасываться исключение
        assertThatThrownBy(() -> userStorage.removeFriend(999, 1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    void getFriends_ShouldReturnAllFriends() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(1, 3);
        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId)
                .containsExactlyInAnyOrder(2, 3);
    }

    @Test
    void getFriends_WithNoFriends_ShouldReturnEmptyList() {
        List<User> friends = userStorage.getFriends(1);
        assertThat(friends).isEmpty();
    }

    @Test
    void getCommonFriends_ShouldReturnCommonFriends() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);
        List<User> commonFriends = userStorage.getCommonFriends(1, 3);
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getCommonFriends_WithNoCommonFriends_ShouldReturnEmptyList() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 1);
        List<User> commonFriends = userStorage.getCommonFriends(1, 3);
        assertThat(commonFriends).isEmpty();
    }

    @Test
    void getCommonFriends_WithInvalidUserId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userStorage.getCommonFriends(999, 1))
                .isInstanceOf(NotFoundException.class);
    }
}