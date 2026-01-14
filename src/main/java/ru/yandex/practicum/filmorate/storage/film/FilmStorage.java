package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    String remove(Integer id);

    Film filmById(Integer id);

    Collection<Film> findAll();

    Film validateFilmExists(Integer id);
}
