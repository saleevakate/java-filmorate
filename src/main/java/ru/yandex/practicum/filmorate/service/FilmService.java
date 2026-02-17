package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, MpaDbStorage mpaStorage, GenreDbStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.validateFilmExists(filmId);
        userStorage.validateUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        filmStorage.validateFilmExists(filmId);
        userStorage.validateUserExists(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Получение списка {} популярных фильмов.", count);
        return filmStorage.getTopFilms(count);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilmInTheFuture(film);
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.findById(film.getMpa().getId());
        } else {
            throw new ValidationException("MPA должен быть указан");
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("ID жанра не может быть null");
                }
                if (!genreStorage.existsById(genre.getId())) {
                    throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
                }
            }
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public String remove(Integer id) {
        return filmStorage.remove(id);
    }

    public Film filmById(Integer id) {
        return filmStorage.filmById(id);
    }

    private void validateFilmInTheFuture(Film film) {
        final LocalDate MIN_RELEASE_DATE =
                LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше минимально допустимой даты {}",
                    film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Какато фигня");
        }
    }

}