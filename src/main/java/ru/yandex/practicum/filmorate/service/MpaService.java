package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        log.info("Запрос на получение всех рейтингов MPA");
        return mpaStorage.findAll();
    }

    public Mpa getMpaById(Integer id) {
        log.info("Запрос на получение рейтинга MPA с ID: {}", id);
        return mpaStorage.findById(id);
    }
}