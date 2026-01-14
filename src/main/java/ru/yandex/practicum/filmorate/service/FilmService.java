package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean addLike(Integer filmId, Integer userId) {
        filmStorage.validateFilmExists(filmId);
        Set<Integer> likes = filmLikes.computeIfAbsent(filmId, k -> new HashSet<>());
        if (hasUserLiked(filmId, userId)) {
            return false;
        }
        likes.add(userId);
        return true;
    }

    public boolean removeLike(Integer filmId, Integer userId) {
        Set<Integer> likes = filmLikes.get(filmId);
        if (!hasUserLiked(filmId, userId)) {
            return false;
        }
        likes.remove(userId);
        return true;
    }

    public List<Film> getTop10Films() {
        return filmStorage.findAll().stream()
                .map(film -> new Object() {
                    final Film topFilm = film;
                    final int likeCount = getLikeCount(topFilm.getId());
                })
                .sorted((a, b) -> Integer.compare(b.likeCount, a.likeCount))
                .limit(10)
                .map(obj -> obj.topFilm)
                .collect(Collectors.toList());
    }

    public int getLikeCount(Integer filmId) {
        return filmLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }

    public boolean hasUserLiked(Integer filmId, Integer userId) {
        Set<Integer> likes = filmLikes.get(filmId);
        return likes != null && likes.contains(userId);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
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

    public Film validateFilmExists(Integer id) {
        return filmStorage.validateFilmExists(id);
    }
}
