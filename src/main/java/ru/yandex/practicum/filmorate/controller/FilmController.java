package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение списка всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начало создания нового фильма. Данные: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Начало обновления фильма с ID: {}", newFilm.getId());
        return filmService.update(newFilm);
    }

    @DeleteMapping("/{id}")
    public void remove(@Valid @PathVariable("id") Integer id) {
        log.info("Удаление фильма с ID: {}", id);
        filmService.remove(id);
    }

    @GetMapping("/{id}")
    public Film filmById(@Valid @PathVariable("id") Integer id) {
        log.info("Поиск фильма с ID: {}", id);
        return filmService.filmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @Valid @PathVariable("id") Integer filmId,
            @Valid @PathVariable("userId") Integer userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @Valid @PathVariable("id") Integer filmId,
            @Valid @PathVariable("userId") Integer userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTop10Films() {
        log.info("Получение списка 10 популярных фильмов.");
        return filmService.getTop10Films();
    }

}
