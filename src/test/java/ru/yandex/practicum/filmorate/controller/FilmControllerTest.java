package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private FilmController filmController = new FilmController();
    ;
    Film film = new Film();

    @BeforeEach
    void setUp() {
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
    }

    @Test
    void findAll() {
        Collection<Film> films = filmController.findAll();
        assertTrue(films.isEmpty());
    }

    @Test
    void create() {
        Film createFilm = filmController.create(film);
        assertEquals("Name", createFilm.getName());
        assertEquals("Description", createFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), createFilm.getReleaseDate());
        assertEquals(90, createFilm.getDuration());
    }

    @Test
    void update() {
        Film createFilm = filmController.create(film);
        Film updatedFilm = new Film();
        updatedFilm.setId(createFilm.getId());
        updatedFilm.setName("New Name");
        updatedFilm.setDescription("New Description");
        updatedFilm.setReleaseDate(LocalDate.of(2005, 1, 1));
        updatedFilm.setDuration(120);
        Film result = filmController.update(updatedFilm);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(LocalDate.of(2005, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
    }

    @Test
    void validateFilmInTheFuture() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }
}
