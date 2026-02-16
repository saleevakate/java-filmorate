package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmDbStorage filmStorage;

    @Mock
    private UserDbStorage userStorage;

    @InjectMocks
    private FilmService filmService;

    @Test
    void addLike_ShouldValidateAndCallStorage() {
        Integer filmId = 1;
        Integer userId = 1;

        filmService.addLike(filmId, userId);

        verify(filmStorage).validateFilmExists(filmId);
        verify(userStorage).validateUserExists(userId);
        verify(filmStorage).addLike(filmId, userId);
    }

    @Test
    void removeLike_ShouldValidateAndCallStorage() {
        Integer filmId = 1;
        Integer userId = 1;

        filmService.removeLike(filmId, userId);

        verify(filmStorage).validateFilmExists(filmId);
        verify(userStorage).validateUserExists(userId);
        verify(filmStorage).removeLike(filmId, userId);
    }

    @Test
    void getTopFilms_ShouldCallStorage() {
        Integer count = 10;
        List<Film> expectedFilms = List.of(new Film(), new Film());
        when(filmStorage.getTopFilms(count)).thenReturn(expectedFilms);

        List<Film> result = filmService.getTopFilms(count);

        assertThat(result).isEqualTo(expectedFilms);
        verify(filmStorage).getTopFilms(count);
    }

    @Test
    void findAll_ShouldCallStorage() {
        Collection<Film> expectedFilms = List.of(new Film());
        when(filmStorage.findAll()).thenReturn(expectedFilms);

        Collection<Film> result = filmService.findAll();

        assertThat(result).isEqualTo(expectedFilms);
        verify(filmStorage).findAll();
    }

    @Test
    void create_ShouldCallStorage() {
        Film film = new Film();
        when(filmStorage.create(film)).thenReturn(film);

        Film result = filmService.create(film);

        assertThat(result).isEqualTo(film);
        verify(filmStorage).create(film);
    }

    @Test
    void update_ShouldCallStorage() {
        Film film = new Film();
        when(filmStorage.update(film)).thenReturn(film);

        Film result = filmService.update(film);

        assertThat(result).isEqualTo(film);
        verify(filmStorage).update(film);
    }

    @Test
    void remove_ShouldCallStorage() {
        Integer id = 1;
        String expected = "Фильм удален";
        when(filmStorage.remove(id)).thenReturn(expected);

        String result = filmService.remove(id);

        assertThat(result).isEqualTo(expected);
        verify(filmStorage).remove(id);
    }

    @Test
    void filmById_ShouldCallStorage() {
        Integer id = 1;
        Film expectedFilm = new Film();
        when(filmStorage.filmById(id)).thenReturn(expectedFilm);

        Film result = filmService.filmById(id);

        assertThat(result).isEqualTo(expectedFilm);
        verify(filmStorage).filmById(id);
    }
}