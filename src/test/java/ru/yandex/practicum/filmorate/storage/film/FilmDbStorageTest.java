package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем все связанные таблицы
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film");
        jdbcTemplate.update("DELETE FROM mpa");
        jdbcTemplate.update("DELETE FROM genre");

        // Сброс счетчиков
        jdbcTemplate.update("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1");

        // Вставка MPA рейтингов
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "G");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "PG");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "PG-13");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "R");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "NC-17");

        // Вставка жанров
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Комедия");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Драма");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Мультфильм");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Триллер");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Документальный");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Боевик");

        // Вставка тестовых фильмов
        jdbcTemplate.update(
                "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                "Интерстеллар", "Космическая одиссея", LocalDate.of(2014, 11, 7), 169, 3);
        jdbcTemplate.update(
                "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                "Дэдпул", "Комедийный боевик", LocalDate.of(2016, 2, 12), 108, 4);
        jdbcTemplate.update(
                "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                "Матрица", "Философский боевик", LocalDate.of(1999, 3, 31), 136, 4);

        // Связывание фильмов с жанрами
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 1, 2); // Драма
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 1, 4); // Триллер
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 2, 1); // Комедия
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 2, 6); // Боевик
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 3, 4); // Триллер
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", 3, 6); // Боевик
    }

    // ==================== ТЕСТЫ НА ЧТЕНИЕ ====================

    @Test
    void findAll_ShouldReturnAllFilms() {
        Collection<Film> films = filmStorage.findAll();

        assertThat(films).isNotNull();
        assertThat(films).hasSize(3);
    }

    @Test
    void filmById_WithValidId_ShouldReturnFilmWithAllData() {
        Film film = filmStorage.filmById(1);

        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Интерстеллар");
        assertThat(film.getDescription()).isEqualTo("Космическая одиссея");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2014, 11, 7));
        assertThat(film.getDuration()).isEqualTo(169);

        // Проверяем MPA
        assertThat(film.getMpa()).isNotNull();
        assertThat(film.getMpa().getId()).isEqualTo(3);
        assertThat(film.getMpa().getName()).isEqualTo("PG-13");

        // Проверяем жанры
        assertThat(film.getGenres()).hasSize(2);
        assertThat(film.getGenres()).extracting(Genre::getId)
                .containsExactlyInAnyOrder(2, 4);
        assertThat(film.getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Драма", "Триллер");
    }

    @Test
    void filmById_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> filmStorage.filmById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с ID 999 не найден");
    }

    // ==================== ТЕСТЫ НА СОЗДАНИЕ ====================

    @Test
    void create_ShouldAddNewFilmWithAllData() {
        Film newFilm = new Film();
        newFilm.setName("Новый фильм");
        newFilm.setDescription("Описание нового фильма");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        Genre genre1 = new Genre();
        genre1.setId(1);
        Genre genre2 = new Genre();
        genre2.setId(2);
        newFilm.setGenres(Set.of(genre1, genre2));

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getId()).isEqualTo(4);
        assertThat(createdFilm.getName()).isEqualTo("Новый фильм");

        Film savedFilm = filmStorage.filmById(4);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getMpa().getId()).isEqualTo(1);
        assertThat(savedFilm.getGenres()).hasSize(2);
        assertThat(savedFilm.getGenres()).extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void create_WithoutMpa_ShouldThrowException() {
        Film newFilm = new Film();
        newFilm.setName("Новый фильм");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(120);

        assertThatThrownBy(() -> filmStorage.create(newFilm))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MPA рейтинг должен быть указан");
    }

    @Test
    void create_WithoutGenres_ShouldCreateFilmWithoutGenres() {
        Film newFilm = new Film();
        newFilm.setName("Новый фильм");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm.getId()).isNotNull();

        Film savedFilm = filmStorage.filmById(createdFilm.getId());
        assertThat(savedFilm.getGenres()).isEmpty();
    }

    @Test
    void create_WithDuplicateGenres_ShouldSaveUniqueGenres() {
        Film newFilm = new Film();
        newFilm.setName("Новый фильм");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        // Используем HashSet вместо Set.of() - он автоматически удалит дубликаты
        Set<Genre> genres = new HashSet<>();

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(1); // Тот же ID
        genre2.setName("Комедия");

        genres.add(genre1);
        genres.add(genre2); // HashSet не добавит дубликат

        newFilm.setGenres(genres);

        Film createdFilm = filmStorage.create(newFilm);

        Film savedFilm = filmStorage.filmById(createdFilm.getId());
        assertThat(savedFilm.getGenres()).hasSize(1);
        assertThat(savedFilm.getGenres().iterator().next().getId()).isEqualTo(1);
    }

    // ==================== ТЕСТЫ НА ОБНОВЛЕНИЕ ====================

    @Test
    void update_ShouldModifyExistingFilm() {
        Film film = filmStorage.filmById(1);
        film.setName("Обновленное название");
        film.setDescription("Обновленное описание");

        // Изменяем жанры
        Genre genre = new Genre();
        genre.setId(1); // Только комедия
        film.setGenres(Set.of(genre));

        Film updatedFilm = filmStorage.update(film);

        assertThat(updatedFilm.getName()).isEqualTo("Обновленное название");
        assertThat(updatedFilm.getDescription()).isEqualTo("Обновленное описание");

        Film retrievedFilm = filmStorage.filmById(1);
        assertThat(retrievedFilm.getName()).isEqualTo("Обновленное название");
        assertThat(retrievedFilm.getGenres()).hasSize(1);
        assertThat(retrievedFilm.getGenres().iterator().next().getId()).isEqualTo(1);
    }

    @Test
    void update_WithInvalidId_ShouldThrowNotFoundException() {
        Film film = new Film();
        film.setId(999);
        film.setName("Тест");
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        assertThatThrownBy(() -> filmStorage.update(film))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с ID 999 не найден");
    }

    @Test
    void update_WithoutMpa_ShouldThrowException() {
        Film film = filmStorage.filmById(1);
        film.setMpa(null);

        assertThatThrownBy(() -> filmStorage.update(film))
                .isInstanceOf(IllegalArgumentException.class)  // или NotFoundException?
                .hasMessageContaining("MPA рейтинг должен быть указан");
    }

    @Test
    void update_RemoveAllGenres_ShouldWork() {
        Film film = filmStorage.filmById(1);
        film.setGenres(Set.of()); // Удаляем все жанры

        Film updatedFilm = filmStorage.update(film);

        Film retrievedFilm = filmStorage.filmById(1);
        assertThat(retrievedFilm.getGenres()).isEmpty();
    }

    // ==================== ТЕСТЫ НА УДАЛЕНИЕ ====================

    @Test
    void remove_ShouldDeleteFilm() {
        filmStorage.remove(1);

        assertThatThrownBy(() -> filmStorage.filmById(1))
                .isInstanceOf(NotFoundException.class);

        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void remove_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> filmStorage.remove(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с ID 999 не найден");
    }

    @Test
    void remove_ShouldCascadeDeleteGenres() {
        filmStorage.remove(1);

        // Проверяем, что связи с жанрами удалились
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genres WHERE film_id = ?",
                Integer.class, 1);
        assertThat(count).isZero();
    }

    @Test
    void remove_ShouldCascadeDeleteLikes() {
        // Добавляем лайк
        filmStorage.addLike(1, 1);

        filmStorage.remove(1);

        // Проверяем, что лайки удалились
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ?",
                Integer.class, 1);
        assertThat(count).isZero();
    }

    // ==================== ТЕСТЫ НА ЛАЙКИ ====================

    @Test
    void addLike_ShouldAddLikeToFilm() {
        filmStorage.addLike(1, 1);

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                Integer.class, 1, 1);
        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    void addLike_DuplicateLike_ShouldNotCreateDuplicate() {
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 1); // Повторный лайк

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ?",
                Integer.class, 1);
        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    void removeLike_ShouldRemoveLikeFromFilm() {
        filmStorage.addLike(1, 1);
        filmStorage.removeLike(1, 1);

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ?",
                Integer.class, 1);
        assertThat(likeCount).isZero();
    }

    @Test
    void removeLike_WithNonExistentLike_ShouldDoNothing() {
        filmStorage.removeLike(1, 999);

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes",
                Integer.class);
        assertThat(likeCount).isZero();
    }

    // ==================== ТЕСТЫ НА ПОЛУЧЕНИЕ ТОП ФИЛЬМОВ ====================

    @Test
    void getTopFilms_ShouldReturnFilmsOrderedByLikes() {
        // Добавляем лайки
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2); // Фильм 1: 2 лайка
        filmStorage.addLike(2, 3); // Фильм 2: 1 лайк
        filmStorage.addLike(3, 1); // Фильм 3: 1 лайк

        List<Film> topFilms = filmStorage.getTopFilms(10);

        assertThat(topFilms).isNotEmpty();
        assertThat(topFilms.get(0).getId()).isEqualTo(1);
        assertThat(topFilms.get(0).getName()).isEqualTo("Интерстеллар");
    }

    @Test
    void getTopFilms_WithLimit_ShouldReturnLimitedNumber() {
        filmStorage.addLike(1, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 3);

        List<Film> topFilms = filmStorage.getTopFilms(2);

        assertThat(topFilms).hasSize(2);
    }

    @Test
    void getTopFilms_WithNoLikes_ShouldReturnAllFilms() {
        List<Film> topFilms = filmStorage.getTopFilms(10);

        assertThat(topFilms).hasSize(3);
    }

    @Test
    void getTopFilms_ShouldLoadGenresAndMpa() {
        filmStorage.addLike(1, 1);

        List<Film> topFilms = filmStorage.getTopFilms(10);
        Film topFilm = topFilms.get(0);

        assertThat(topFilm.getMpa()).isNotNull();
        assertThat(topFilm.getGenres()).isNotEmpty();
    }

    // ==================== ТЕСТЫ НА ВАЛИДАЦИЮ ====================

    @Test
    void validateFilmExists_WithValidId_ShouldReturnFilm() {
        Film film = filmStorage.validateFilmExists(1);
        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);
    }

    @Test
    void validateFilmExists_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> filmStorage.validateFilmExists(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм с ID 999 не найден");
    }

    // ==================== ТЕСТЫ НА ЗАГРУЗКУ СВЯЗЕЙ ====================

    @Test
    void filmById_ShouldLoadGenresInCorrectOrder() {
        Film film = filmStorage.filmById(2); // Дэдпул

        List<Genre> genres = film.getGenres().stream().toList();
        // Проверяем сортировку по genre_id
        assertThat(genres.get(0).getId()).isLessThan(genres.get(1).getId());
    }

    @Test
    void filmById_ShouldLoadMpaCorrectly() {
        Film film = filmStorage.filmById(1);

        assertThat(film.getMpa()).isNotNull();
        assertThat(film.getMpa().getId()).isEqualTo(3);
        assertThat(film.getMpa().getName()).isEqualTo("PG-13");
    }

    @Test
    void findAll_ShouldLoadAllDataForEachFilm() {
        Collection<Film> films = filmStorage.findAll();

        films.forEach(film -> {
            assertThat(film.getMpa()).isNotNull();
            assertThat(film.getGenres()).isNotNull();
        });
    }

    // ==================== ТЕСТЫ НА ГРАНИЧНЫЕ ЗНАЧЕНИЯ ====================

    @Test
    void create_WithMaxDescriptionLength_ShouldWork() {
        String longDescription = "a".repeat(200);

        Film newFilm = new Film();
        newFilm.setName("Тест");
        newFilm.setDescription(longDescription);
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(100);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        Film createdFilm = filmStorage.create(newFilm);

        Film savedFilm = filmStorage.filmById(createdFilm.getId());
        assertThat(savedFilm.getDescription()).hasSize(200);
    }

    @Test
    void create_WithMinDuration_ShouldWork() {
        Film newFilm = new Film();
        newFilm.setName("Тест");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(1); // Минимальная длительность

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm.getDuration()).isEqualTo(1);
    }

    @Test
    void create_WithZeroDuration_ShouldFail() {
        Film newFilm = new Film();
        newFilm.setName("Тест");
        newFilm.setDescription("Описание");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(0);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        // Проверяем, что БД не позволяет вставить отрицательную длительность
        assertThatThrownBy(() -> filmStorage.create(newFilm))
                .isInstanceOf(Exception.class);
    }
}