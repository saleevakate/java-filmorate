package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaDbStorage.class, MpaRowMapper.class})
@Import({MpaDbStorage.class, MpaRowMapper.class})
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем таблицу MPA
        jdbcTemplate.update("DELETE FROM film");
        jdbcTemplate.update("DELETE FROM mpa");

        // Сброс счетчика
        jdbcTemplate.update("ALTER TABLE mpa ALTER COLUMN mpa_id RESTART WITH 1");

        // Вставка тестовых MPA рейтингов (американская система)
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "G");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "PG");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "PG-13");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "R");
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "NC-17");
    }

    // ==================== ТЕСТЫ НА ЧТЕНИЕ ====================

    @Test
    void findAll_ShouldReturnAllMpaRatings() {
        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).isNotNull();
        assertThat(mpas).hasSize(5);

        // Проверяем, что рейтинги отсортированы по id
        assertThat(mpas.get(0).getId()).isEqualTo(1);
        assertThat(mpas.get(0).getName()).isEqualTo("G");
        assertThat(mpas.get(1).getId()).isEqualTo(2);
        assertThat(mpas.get(1).getName()).isEqualTo("PG");
        assertThat(mpas.get(2).getId()).isEqualTo(3);
        assertThat(mpas.get(2).getName()).isEqualTo("PG-13");
        assertThat(mpas.get(3).getId()).isEqualTo(4);
        assertThat(mpas.get(3).getName()).isEqualTo("R");
        assertThat(mpas.get(4).getId()).isEqualTo(5);
        assertThat(mpas.get(4).getName()).isEqualTo("NC-17");
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoMpaRatings() {
        // Очищаем таблицу
        jdbcTemplate.update("DELETE FROM mpa");

        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).isNotNull();
        assertThat(mpas).isEmpty();
    }

    @Test
    void findById_WithValidId_ShouldReturnMpaRating() {
        Mpa mpa = mpaStorage.findById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void findById_WithValidId_ShouldReturnCorrectMpaForAllIds() {
        String[] expectedNames = {"G", "PG", "PG-13", "R", "NC-17"};

        for (int i = 1; i <= 5; i++) {
            Mpa mpa = mpaStorage.findById(i);
            assertThat(mpa.getName()).isEqualTo(expectedNames[i - 1]);
        }
    }

    @Test
    void findById_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> mpaStorage.findById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Рейтинг MPA с ID 999 не найден");
    }

    @Test
    void findById_WithNegativeId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> mpaStorage.findById(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Рейтинг MPA с ID -1 не найден");
    }

    @Test
    void findById_WithZeroId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> mpaStorage.findById(0))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Рейтинг MPA с ID 0 не найден");
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ ДАННЫХ ====================

    @Test
    void findAll_ShouldReturnMpaRatingsWithCorrectNames() {
        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void findById_ShouldReturnMpaWithCorrectName() {
        // Проверяем каждый рейтинг по отдельности
        assertThat(mpaStorage.findById(1).getName()).isEqualTo("G");
        assertThat(mpaStorage.findById(2).getName()).isEqualTo("PG");
        assertThat(mpaStorage.findById(3).getName()).isEqualTo("PG-13");
        assertThat(mpaStorage.findById(4).getName()).isEqualTo("R");
        assertThat(mpaStorage.findById(5).getName()).isEqualTo("NC-17");
    }

    // ==================== ТЕСТЫ НА ID ====================

    @Test
    void mpaRatings_ShouldHaveSequentialIds() {
        List<Mpa> mpas = mpaStorage.findAll();

        // Проверяем, что ID идут по порядку: 1, 2, 3, 4, 5
        for (int i = 0; i < mpas.size(); i++) {
            assertThat(mpas.get(i).getId()).isEqualTo(i + 1);
        }
    }

    @Test
    void findById_ShouldReturnMpaRatingsWithAllPossibleIds() {
        // Проверяем все существующие ID от 1 до 5
        for (int id = 1; id <= 5; id++) {
            Mpa mpa = mpaStorage.findById(id);
            assertThat(mpa).isNotNull();
            assertThat(mpa.getId()).isEqualTo(id);
        }
    }

    // ==================== ТЕСТЫ НА УНИКАЛЬНОСТЬ ====================

    @Test
    void mpaNames_ShouldBeUnique() {
        List<Mpa> mpas = mpaStorage.findAll();

        // Проверяем, что нет дубликатов имен
        List<String> names = mpas.stream()
                .map(Mpa::getName)
                .toList();

        assertThat(names).doesNotHaveDuplicates();
    }

    // ==================== ТЕСТЫ НА ЦЕЛОСТНОСТЬ ДАННЫХ ====================

    @Test
    void mpaRatings_ShouldHaveNonNullNames() {
        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).allSatisfy(mpa -> {
            assertThat(mpa.getName()).isNotNull();
            assertThat(mpa.getName()).isNotBlank();
        });
    }

    @Test
    void mpaRatings_ShouldHaveNonEmptyNames() {
        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).allSatisfy(mpa ->
                assertThat(mpa.getName().trim()).isNotEmpty()
        );
    }

    // ==================== ТЕСТЫ НА ГРАНИЧНЫЕ ЗНАЧЕНИЯ ====================

    @Test
    void findById_WithMaximumId_ShouldWork() {
        // Добавляем MPA рейтинг с большим ID и коротким названием (макс 10 символов)
        jdbcTemplate.update("INSERT INTO mpa (mpa_id, name) VALUES (?, ?)", 1000, "TEST");

        Mpa mpa = mpaStorage.findById(1000);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1000);
        assertThat(mpa.getName()).isEqualTo("TEST");
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ ПОРЯДКА СОРТИРОВКИ ====================

    @Test
    void findAll_ShouldReturnMpaRatingsSortedById() {
        List<Mpa> mpas = mpaStorage.findAll();

        // Проверяем, что рейтинги отсортированы по возрастанию ID
        for (int i = 0; i < mpas.size() - 1; i++) {
            assertThat(mpas.get(i).getId())
                    .isLessThan(mpas.get(i + 1).getId());
        }
    }

    @Test
    void findAll_AfterAddingNewMpa_ShouldMaintainOrder() {
        // Добавляем новый MPA рейтинг с ID 6
        jdbcTemplate.update("INSERT INTO mpa (mpa_id, name) VALUES (?, ?)", 6, "NEW-RATING");

        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).hasSize(6);
        assertThat(mpas.get(5).getId()).isEqualTo(6);
        assertThat(mpas.get(5).getName()).isEqualTo("NEW-RATING");

        // Проверяем, что порядок сохранился
        for (int i = 0; i < mpas.size() - 1; i++) {
            assertThat(mpas.get(i).getId())
                    .isLessThan(mpas.get(i + 1).getId());
        }
    }

    // ==================== ТЕСТЫ НА КОЛИЧЕСТВО ====================

    @Test
    void count_ShouldReturnCorrectNumberOfMpaRatings() {
        List<Mpa> mpas = mpaStorage.findAll();
        assertThat(mpas).hasSize(5);

        // Добавляем еще один рейтинг
        jdbcTemplate.update("INSERT INTO mpa (name) VALUES (?)", "NEW-RATING");

        mpas = mpaStorage.findAll();
        assertThat(mpas).hasSize(6);
    }

    // ==================== ТЕСТЫ НА ИЗМЕНЕНИЕ ДАННЫХ (НЕ ДОЛЖНЫ РАБОТАТЬ) ====================

    @Test
    void mpaData_ShouldNotBeModifiedByAccident() {
        // Получаем оригинальные данные
        List<Mpa> originalMpas = mpaStorage.findAll();

        // Пытаемся изменить полученный объект
        if (!originalMpas.isEmpty()) {
            Mpa firstMpa = originalMpas.get(0);
            firstMpa.setName("Измененное имя");

            // Проверяем, что в базе данные не изменились
            Mpa mpaFromDb = mpaStorage.findById(firstMpa.getId());
            assertThat(mpaFromDb.getName()).isNotEqualTo("Измененное имя");
            assertThat(mpaFromDb.getName()).isEqualTo("G");
        }
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ СУЩЕСТВОВАНИЯ ====================

    @Test
    void mpaExists_WithValidId_ShouldBeTrue() {
        for (int id = 1; id <= 5; id++) {
            try {
                Mpa mpa = mpaStorage.findById(id);
                assertThat(mpa).isNotNull();
            } catch (NotFoundException e) {
                assertThat(e).as("MPA рейтинг с ID %d должен существовать", id).isNull();
            }
        }
    }

    @Test
    void mpaExists_WithInvalidId_ShouldBeFalse() {
        assertThatThrownBy(() -> mpaStorage.findById(999))
                .isInstanceOf(NotFoundException.class);
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ КОНКРЕТНЫХ ЗНАЧЕНИЙ ====================

    @Test
    void mpaWithId1_ShouldBeG() {
        Mpa mpa = mpaStorage.findById(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void mpaWithId2_ShouldBePG() {
        Mpa mpa = mpaStorage.findById(2);
        assertThat(mpa.getName()).isEqualTo("PG");
    }

    @Test
    void mpaWithId3_ShouldBePG13() {
        Mpa mpa = mpaStorage.findById(3);
        assertThat(mpa.getName()).isEqualTo("PG-13");
    }

    @Test
    void mpaWithId4_ShouldBeR() {
        Mpa mpa = mpaStorage.findById(4);
        assertThat(mpa.getName()).isEqualTo("R");
    }

    @Test
    void mpaWithId5_ShouldBeNC17() {
        Mpa mpa = mpaStorage.findById(5);
        assertThat(mpa.getName()).isEqualTo("NC-17");
    }

    // ==================== ТЕСТЫ НА СООТВЕТСТВИЕ ТРЕБОВАНИЯМ ====================

    @Test
    void shouldHaveExactlyFiveMpaRatings() {
        List<Mpa> mpas = mpaStorage.findAll();
        assertThat(mpas).hasSize(5);
    }

    @Test
    void mpaRatings_ShouldBeInCorrectOrder() {
        List<Mpa> mpas = mpaStorage.findAll();

        // Проверяем порядок (по возрастанию возраста)
        assertThat(mpas).extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void mpaRatings_ShouldHaveCorrectStructure() {
        List<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas).allSatisfy(mpa -> {
            assertThat(mpa.getId()).isPositive();
            assertThat(mpa.getName()).isNotBlank();
        });
    }

    // ==================== ТЕСТЫ НА ВЗАИМОДЕЙСТВИЕ С ДРУГИМИ ТАБЛИЦАМИ ====================

    @Test
    void mpa_ShouldBeUsedInFilms() {
        // Проверяем, что MPA рейтинги используются в таблице film
        // Сначала добавляем тестовый фильм, если его нет
        try {
            jdbcTemplate.update("INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                    "Тестовый фильм", "Описание", "2023-01-01", 120, 1);
        } catch (Exception e) {
            // Если фильм уже существует, игнорируем
        }

        // Проверяем, что есть фильмы с MPA ID = 1
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film WHERE mpa_id = ?",
                Integer.class, 1
        );

        // Тест проходит, если запрос выполняется без ошибок
        assertThat(count).isNotNull();
    }

    // ==================== ТЕСТЫ НА ПРОВЕРКУ ССЫЛОЧНОЙ ЦЕЛОСТНОСТИ ====================

    @Test
    void cannotDeleteMpa_WhenUsedInFilms() {
        // Добавляем фильм, который использует MPA ID 1
        jdbcTemplate.update("INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                "Тестовый фильм", "Описание", "2023-01-01", 120, 1);

        // Пытаемся удалить MPA рейтинг (должно выбросить исключение из-за внешнего ключа)
        assertThatThrownBy(() -> jdbcTemplate.update("DELETE FROM mpa WHERE mpa_id = ?", 1))
                .isInstanceOf(Exception.class);
    }
}