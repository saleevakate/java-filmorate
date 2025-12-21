package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {


    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
            log.info("Начало создания нового фильма. Данные: {}", film);
            validateFilm(film);
            int newId = getNextId();
            film.setId(newId);
            films.put(newId, film);
            log.info("Фильм успешно создан. ID: {}, название: {}", newId, film.getName());
            return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
            log.info("Начало обновления фильма с ID: {}", newFilm.getId());
            Film existingFilm = films.get(newFilm.getId());
            if (existingFilm == null) {
                log.warn("Фильм с ID {} не найден в хранилище", newFilm.getId());
                throw new ValidationException("Фильм с указанным ID не найден");
            }
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

    private void validateFilm(Film film) {
        log.debug("Начинается валидация фильма: {}", film);

        if (film == null) {
            log.warn("Переданный объект фильма равен null");
            throw new ValidationException("Фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма пустое или null");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Описание фильма превышает 200 символов. Длина: {}", film.getDescription().length());
            throw new ValidationException("Описание не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Дата релиза фильма равна null");
            throw new ValidationException("Дата релиза не может быть null");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата релиза {} раньше минимально допустимой даты {}",
                    film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null) {
            log.warn("Продолжительность фильма равна null");
            throw new ValidationException("Продолжительность не может быть null");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма отрицательная или равна нулю: {}", film.getDuration());
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }

        log.debug("Валидация фильма завершена успешно: {}", film);
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
