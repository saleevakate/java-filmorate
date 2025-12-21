package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void nameIsNull() {
        Film film = new Film();
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Название не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void nameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Название не может быть пустым".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void longDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Описание не может превышать 200 символов".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void releaseDataIsNull() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setDuration(90);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Дата релиза не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void durationIsNull() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Продолжительность не может быть null".equals(violation.getMessage())) {
                return;
            }
        }
    }

    @Test
    void durationIsPositive() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-90);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> violation : violations) {
            if ("Продолжительность должна быть положительным числом".equals(violation.getMessage())) {
                return;
            }
        }
    }
}
