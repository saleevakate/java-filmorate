package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {GenreDbStorage.class, GenreRowMapper.class})
@Import({GenreDbStorage.class, GenreRowMapper.class})
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем таблицу жанров
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM genre");

        // Сброс счетчика
        jdbcTemplate.update("ALTER TABLE genre ALTER COLUMN genre_id RESTART WITH 1");

        // Вставка тестовых жанров
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Комедия");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Драма");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Мультфильм");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Триллер");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Документальный");
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Боевик");
    }

    // ==================== ТЕСТЫ НА ЧТЕНИЕ ====================

    @Test
    void findAll_ShouldReturnAllGenres() {
        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).isNotNull();
        assertThat(genres).hasSize(6);

        // Проверяем, что жанры отсортированы по id
        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getName()).isEqualTo("Комедия");
        assertThat(genres.get(1).getId()).isEqualTo(2);
        assertThat(genres.get(1).getName()).isEqualTo("Драма");
        assertThat(genres.get(2).getId()).isEqualTo(3);
        assertThat(genres.get(2).getName()).isEqualTo("Мультфильм");
        assertThat(genres.get(3).getId()).isEqualTo(4);
        assertThat(genres.get(3).getName()).isEqualTo("Триллер");
        assertThat(genres.get(4).getId()).isEqualTo(5);
        assertThat(genres.get(4).getName()).isEqualTo("Документальный");
        assertThat(genres.get(5).getId()).isEqualTo(6);
        assertThat(genres.get(5).getName()).isEqualTo("Боевик");
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoGenres() {
        // Очищаем таблицу
        jdbcTemplate.update("DELETE FROM genre");

        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).isNotNull();
        assertThat(genres).isEmpty();
    }

    @Test
    void findById_WithValidId_ShouldReturnGenre() {
        Genre genre = genreStorage.findById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void findById_WithValidId_ShouldReturnCorrectGenreForAllIds() {
        String[] expectedNames = {
                "Комедия", "Драма", "Мультфильм",
                "Триллер", "Документальный", "Боевик"
        };

        for (int i = 1; i <= 6; i++) {
            Genre genre = genreStorage.findById(i);
            assertThat(genre.getName()).isEqualTo(expectedNames[i - 1]);
        }
    }

    @Test
    void findById_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> genreStorage.findById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр с ID 999 не найден");
    }

    @Test
    void findById_WithNegativeId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> genreStorage.findById(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр с ID -1 не найден");
    }

    @Test
    void findById_WithZeroId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> genreStorage.findById(0))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр с ID 0 не найден");
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ ДАННЫХ ====================

    @Test
    void findAll_ShouldReturnGenresWithCorrectNames() {
        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).extracting(Genre::getName)
                .containsExactly(
                        "Комедия",
                        "Драма",
                        "Мультфильм",
                        "Триллер",
                        "Документальный",
                        "Боевик"
                );
    }

    @Test
    void findById_ShouldReturnGenreWithCorrectName() {
        // Проверяем каждый жанр по отдельности
        assertThat(genreStorage.findById(1).getName()).isEqualTo("Комедия");
        assertThat(genreStorage.findById(2).getName()).isEqualTo("Драма");
        assertThat(genreStorage.findById(3).getName()).isEqualTo("Мультфильм");
        assertThat(genreStorage.findById(4).getName()).isEqualTo("Триллер");
        assertThat(genreStorage.findById(5).getName()).isEqualTo("Документальный");
        assertThat(genreStorage.findById(6).getName()).isEqualTo("Боевик");
    }

    // ==================== ТЕСТЫ НА ID ====================

    @Test
    void genres_ShouldHaveSequentialIds() {
        List<Genre> genres = genreStorage.findAll();

        // Проверяем, что ID идут по порядку: 1, 2, 3, 4, 5, 6
        for (int i = 0; i < genres.size(); i++) {
            assertThat(genres.get(i).getId()).isEqualTo(i + 1);
        }
    }

    @Test
    void findById_ShouldReturnGenresWithAllPossibleIds() {
        // Проверяем все существующие ID от 1 до 6
        for (int id = 1; id <= 6; id++) {
            Genre genre = genreStorage.findById(id);
            assertThat(genre).isNotNull();
            assertThat(genre.getId()).isEqualTo(id);
        }
    }

    // ==================== ТЕСТЫ НА УНИКАЛЬНОСТЬ ====================

    @Test
    void genreNames_ShouldBeUnique() {
        List<Genre> genres = genreStorage.findAll();

        // Проверяем, что нет дубликатов имен
        List<String> names = genres.stream()
                .map(Genre::getName)
                .toList();

        assertThat(names).doesNotHaveDuplicates();
    }

    // ==================== ТЕСТЫ НА ЦЕЛОСТНОСТЬ ДАННЫХ ====================

    @Test
    void genres_ShouldHaveNonNullNames() {
        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).allSatisfy(genre -> {
            assertThat(genre.getName()).isNotNull();
            assertThat(genre.getName()).isNotBlank();
        });
    }

    @Test
    void genres_ShouldHaveNonEmptyNames() {
        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).allSatisfy(genre ->
                assertThat(genre.getName().trim()).isNotEmpty()
        );
    }

    // ==================== ТЕСТЫ НА ГРАНИЧНЫЕ ЗНАЧЕНИЯ ====================

    @Test
    void findById_WithMaximumId_ShouldWork() {
        // Добавляем жанр с большим ID
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (?, ?)", 1000, "Тестовый жанр");

        Genre genre = genreStorage.findById(1000);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1000);
        assertThat(genre.getName()).isEqualTo("Тестовый жанр");
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ ПОРЯДКА СОРТИРОВКИ ====================

    @Test
    void findAll_ShouldReturnGenresSortedById() {
        List<Genre> genres = genreStorage.findAll();

        // Проверяем, что жанры отсортированы по возрастанию ID
        for (int i = 0; i < genres.size() - 1; i++) {
            assertThat(genres.get(i).getId())
                    .isLessThan(genres.get(i + 1).getId());
        }
    }

    @Test
    void findAll_AfterAddingNewGenre_ShouldMaintainOrder() {
        // Добавляем новый жанр с ID 7
        jdbcTemplate.update("INSERT INTO genre (genre_id, name) VALUES (?, ?)", 7, "Фэнтези");

        List<Genre> genres = genreStorage.findAll();

        assertThat(genres).hasSize(7);
        assertThat(genres.get(6).getId()).isEqualTo(7);
        assertThat(genres.get(6).getName()).isEqualTo("Фэнтези");

        // Проверяем, что порядок сохранился
        for (int i = 0; i < genres.size() - 1; i++) {
            assertThat(genres.get(i).getId())
                    .isLessThan(genres.get(i + 1).getId());
        }
    }

    // ==================== ТЕСТЫ НА КОЛИЧЕСТВО ====================

    @Test
    void count_ShouldReturnCorrectNumberOfGenres() {
        List<Genre> genres = genreStorage.findAll();
        assertThat(genres).hasSize(6);

        // Добавляем еще один жанр
        jdbcTemplate.update("INSERT INTO genre (name) VALUES (?)", "Фэнтези");

        genres = genreStorage.findAll();
        assertThat(genres).hasSize(7);
    }

    // ==================== ТЕСТЫ НА ИЗМЕНЕНИЕ ДАННЫХ (НЕ ДОЛЖНЫ РАБОТАТЬ) ====================

    // Примечание: В хранилище GenreDbStorage нет методов для создания/обновления/удаления,
    // так как жанры - это справочная информация. Эти тесты проверяют, что данные нельзя изменить
    // через существующие методы (но можно через прямое JDBC, что используется для тестов)

    @Test
    void genreData_ShouldNotBeModifiedByAccident() {
        // Получаем оригинальные данные
        List<Genre> originalGenres = genreStorage.findAll();

        // Пытаемся изменить полученный объект
        if (!originalGenres.isEmpty()) {
            Genre firstGenre = originalGenres.get(0);
            firstGenre.setName("Измененное имя");

            // Проверяем, что в базе данные не изменились
            Genre genreFromDb = genreStorage.findById(firstGenre.getId());
            assertThat(genreFromDb.getName()).isNotEqualTo("Измененное имя");
        }
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ СУЩЕСТВОВАНИЯ ====================

    @Test
    void genreExists_WithValidId_ShouldBeTrue() {
        for (int id = 1; id <= 6; id++) {
            try {
                Genre genre = genreStorage.findById(id);
                assertThat(genre).isNotNull();
            } catch (NotFoundException e) {
                assertThat(e).as("Жанр с ID %d должен существовать", id).isNull();
            }
        }
    }

    @Test
    void genreExists_WithInvalidId_ShouldBeFalse() {
        assertThatThrownBy(() -> genreStorage.findById(999))
                .isInstanceOf(NotFoundException.class);
    }

}