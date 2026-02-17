package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class})
@Import({UserDbStorage.class, UserRowMapper.class})
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    @Test
    void testFindUserById_WithValidId_ShouldReturnUser() {
        // Предполагаем, что в базе есть пользователь с ID 1 (из data.sql)
        User user = userStorage.userById(1);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    void testFindUserById_WithInvalidId_ShouldThrowNotFoundException() {
        assertThatThrownBy(() -> userStorage.userById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }
}