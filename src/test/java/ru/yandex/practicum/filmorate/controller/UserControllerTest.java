package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController = new UserController();
    private User user = new User();

    @BeforeEach
    void setUp() {
        user.setName("Test User");
        user.setLogin("userlogin");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void findAll() {
        Collection<User> users = userController.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void create() {
        User createdUser = userController.create(user);
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals("userlogin", createdUser.getLogin());
        assertEquals("Test User", createdUser.getName());
        assertEquals(LocalDate.of(1990, 1, 1), createdUser.getBirthday());
    }

    @Test
    void update() {
        User createdUser = userController.create(user);
        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setName("New Name");
        updatedUser.setBirthday(LocalDate.of(1995, 5, 5));
        User result = userController.update(updatedUser);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newlogin", result.getLogin());
        assertEquals("New Name", result.getName());
        assertEquals(LocalDate.of(1995, 5, 5), result.getBirthday());
    }
}
