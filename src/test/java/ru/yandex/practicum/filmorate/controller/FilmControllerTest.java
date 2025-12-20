package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void findAll() {
        Collection<Film> films = filmController.findAll();
        assertTrue(films.isEmpty());
    }

    @Test
    void createNameNotIsEmpty() {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDateTime.of(2000, 1, 1, 0, 0));
        film.setDuration(Duration.ofMinutes(90));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void createLongDescription() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDateTime.of(2000, 1, 1, 0, 0));
        film.setDuration(Duration.ofMinutes(90));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Описание не может превышать 200 символов", exception.getMessage());
    }

    @Test
    void createDescription200Chars() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDateTime.of(2000, 1, 1, 0, 0));
        film.setDuration(Duration.ofMinutes(90));
        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void createReleaseData() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDateTime.of(1895, 12, 27, 0, 0));
        film.setDuration(Duration.ofMinutes(90));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void createDurationPositive() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDateTime.of(2000, 1, 1, 0, 0));
        film.setDuration(Duration.ofMinutes(-90));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Продолжительность должна быть положительным числом", exception.getMessage());
    }
}
