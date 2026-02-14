package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        Set<Integer> likes = filmLikes.computeIfAbsent(filmId, k -> new HashSet<>());
        if (hasUserLiked(filmId, userId)) {
            return;
        }
        likes.add(userId);
        log.info("Лайк добавлен: фильм {} ← пользователь {}", filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        filmStorage.validateFilmExists(filmId);
        userService.validateUserExists(userId);
        Set<Integer> likes = filmLikes.get(filmId);
        if (!hasUserLiked(filmId, userId)) {
            return;
        }
        likes.remove(userId);
        log.info("Лайк удалён: фильм {} ← пользователь {}", filmId, userId);
    }

    public List<Film> getTop10Films() {
        log.info("Получен список 10 популярных фильмов.");
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

    private int getLikeCount(Integer filmId) {
        return filmLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }

    private boolean hasUserLiked(Integer filmId, Integer userId) {
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

}
