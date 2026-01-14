package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        validateFilmInTheFuture(film);
        int newId = getNextId();
        film.setId(newId);
        films.put(newId, film);
        log.info("Фильм успешно создан. ID: {}, название: {}", newId, film.getName());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film existingFilm = validateFilmExists(newFilm.getId()); //проверка наличия фильма теперь здесь
        if (newFilm.getName() != null) {
            existingFilm.setName(newFilm.getName());
            log.debug("Для фильма ID {} обновлено название: {}", newFilm.getId(), newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            existingFilm.setDescription(newFilm.getDescription());
            log.debug("Для фильма ID {} обновлено описание", newFilm.getId());
        }
        if (newFilm.getReleaseDate() != null) {
            existingFilm.setReleaseDate(newFilm.getReleaseDate());
            log.debug("Для фильма ID {} обновлена дата релиза: {}", newFilm.getId(), newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            existingFilm.setDuration(newFilm.getDuration());
            log.debug("Для фильма ID {} обновлена продолжительность: {}", newFilm.getId(), newFilm.getDuration());
        }
        log.info("Фильм ID {} успешно обновлен", newFilm.getId());
        return existingFilm;
    }

    @Override
    public String remove(Integer id) {
        Film film = validateFilmExists(id);
        log.info("Удаление фильма. Данные: {}", film);
        films.remove(id);
        return "Фильм удален";
    }

    @Override
    public Film filmById(Integer id) {
        return validateFilmExists(id);
    }

    @Override
    public Film validateFilmExists(Integer id) {
        Film filmById = films.get(id);
        if (filmById == null) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден");
        }
        return filmById;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Получен список всех фильмов");
        return films.values();
    }

    private void validateFilmInTheFuture(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше минимально допустимой даты {}",
                    film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        int nextId = ++currentMaxId;
        log.debug("Сгенерирован новый ID для фильма: {}", nextId);
        return nextId;
    }


}
