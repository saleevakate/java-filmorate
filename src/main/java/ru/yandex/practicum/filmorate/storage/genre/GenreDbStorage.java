package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM Genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Genre findById(Integer id) {
        String sql = "SELECT * FROM Genre WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, id);
        if (genres.isEmpty()) {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
        return genres.get(0);
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM genre WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}