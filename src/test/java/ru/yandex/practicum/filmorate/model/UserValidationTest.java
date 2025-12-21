package ru.yandex.practicum.filmorate.model;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void emailIsNull() {
        User user = new User();
        user.setLogin("userlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Электронная почта не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void emailIsEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("userlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Электронная почта не может быть пустой".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void dogInTheMail() {
        User user = new User();
        user.setEmail("test.example.com");
        user.setLogin("userlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Электронная почта должна содержать символ @".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void loginIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Логин не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void loginIsEmpty() {
        User user = new User();
        user.setLogin("");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Логин не может быть пустым".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void spaceInLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Логин не может содержать пробелы".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void birthdayIsNull() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("userlogin");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Дата рождения не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void birthdayInTheFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("userlogin");
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            if ("Дата рождения не может быть в будущем".equals(violation.getMessage())) {
                return;
            }
        }
    }
}
