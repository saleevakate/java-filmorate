package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM Mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Mpa findById(Integer id) {
        String sql = "SELECT * FROM Mpa WHERE mpa_id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sql, mpaRowMapper, id);
        if (mpas.isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с ID " + id + " не найден");
        }
        return mpas.get(0);
    }
}