package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Получение фильма с ID: {}", id);
        Film film = filmService.filmById(id);
        sortGenres(film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        Collection<Film> films = filmService.findAll();
        films.forEach(this::sortGenres);
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создание фильма: {}", film);
        Film createdFilm = filmService.create(film);
        sortGenres(createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма с ID: {}", film.getId());
        Film updatedFilm = filmService.update(film);
        sortGenres(updatedFilm);
        return updatedFilm;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получение {} популярных фильмов", count);
        List<Film> films = filmService.getTopFilms(count);
        films.forEach(this::sortGenres);
        return films;
    }

    private void sortGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparing(Genre::getId));
            sortedGenres.addAll(film.getGenres());
            film.setGenres(sortedGenres);
        }
    }
}