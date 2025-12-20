package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        User user = new User();
    }

    @Test
    void findAll() {
        Collection<User> users = userController.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void createMailIsNotEmpty() {
        User user = new User();
        user.setLogin("testlogin");
        user.setBirthday(LocalDateTime.of(1990, 1, 1, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );
        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    void createDogInTheMail() {
        User user = new User();
        user.setEmail("test.example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDateTime.of(1990, 1, 1, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );
        assertEquals("Электронная почта должна содержать символ @", exception.getMessage());
    }

    @Test
    void createEmptyLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(LocalDateTime.of(1990, 1, 1, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void createSpaceInLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setBirthday(LocalDateTime.of(1990, 1, 1, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );
        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    void createNameIsLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDateTime.of(1990, 1, 1, 1, 1));

        User createdUser = userController.create(user);

        assertEquals(1, createdUser.getId());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("testlogin", createdUser.getLogin());
        assertEquals("testlogin", createdUser.getName());
        assertEquals(LocalDateTime.of(1990, 1, 1, 1, 1), createdUser.getBirthday());
    }

    @Test
    void createBirthdayInTheFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDateTime.now().plusDays(1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
