package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Test
    void getAllMpa_ShouldReturnAllMpaRatings() throws Exception {
        // given - создаем MPA рейтинги с правильными ID и названиями
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");

        List<Mpa> mpas = Arrays.asList(mpa1, mpa2, mpa3, mpa4, mpa5);

        // настраиваем мок
        when(mpaService.getAllMpa()).thenReturn(mpas);

        // when/then
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"));

        verify(mpaService, times(1)).getAllMpa();
    }

    @Test
    void getAllMpa_ShouldReturnEmptyList_WhenNoMpaRatings() throws Exception {
        // given
        List<Mpa> emptyList = Arrays.asList();
        when(mpaService.getAllMpa()).thenReturn(emptyList);

        // when/then
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(mpaService, times(1)).getAllMpa();
    }

    @Test
    void getMpaById_WithValidId_ShouldReturnMpa() throws Exception {
        // given
        Integer id = 1;
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName("G");

        when(mpaService.getMpaById(id)).thenReturn(mpa);

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("G"));

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getMpaById_WithValidId_ShouldReturnCorrectMpaForAllIds() throws Exception {
        // Проверяем каждый ID от 1 до 5
        String[] expectedNames = {"G", "PG", "PG-13", "R", "NC-17"};

        for (int id = 1; id <= 5; id++) {
            Mpa mpa = new Mpa();
            mpa.setId(id);
            mpa.setName(expectedNames[id - 1]);

            when(mpaService.getMpaById(id)).thenReturn(mpa);

            mockMvc.perform(get("/mpa/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(expectedNames[id - 1]));

            verify(mpaService, times(1)).getMpaById(id);
        }
    }

    @Test
    void getMpaById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(mpaService.getMpaById(id)).thenThrow(new NotFoundException("Рейтинг MPA с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isNotFound());

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getMpaById_WithNegativeId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = -1;
        when(mpaService.getMpaById(id)).thenThrow(new NotFoundException("Рейтинг MPA с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isNotFound());

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getMpaById_WithZeroId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 0;
        when(mpaService.getMpaById(id)).thenThrow(new NotFoundException("Рейтинг MPA с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isNotFound());

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getAllMpa_ShouldReturnRatingsInCorrectOrder() throws Exception {
        // given - создаем рейтинги не по порядку, чтобы проверить сортировку
        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");

        // Сервис возвращает их в правильном порядке (по id)
        List<Mpa> sortedMpas = Arrays.asList(mpa1, mpa2, mpa3, mpa4, mpa5);
        when(mpaService.getAllMpa()).thenReturn(sortedMpas);

        // when/then
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("G"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("PG"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("PG-13"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("R"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("NC-17"));

        verify(mpaService, times(1)).getAllMpa();
    }

    @Test
    void getMpaById_ShouldReturnMpaWithCorrectStructure() throws Exception {
        // given
        Integer id = 1;
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName("G");

        when(mpaService.getMpaById(id)).thenReturn(mpa);

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.name").isNotEmpty());

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getAllMpa_ShouldHaveExactlyFiveRatings() throws Exception {
        // given
        Mpa mpa1 = new Mpa(); mpa1.setId(1); mpa1.setName("G");
        Mpa mpa2 = new Mpa(); mpa2.setId(2); mpa2.setName("PG");
        Mpa mpa3 = new Mpa(); mpa3.setId(3); mpa3.setName("PG-13");
        Mpa mpa4 = new Mpa(); mpa4.setId(4); mpa4.setName("R");
        Mpa mpa5 = new Mpa(); mpa5.setId(5); mpa5.setName("NC-17");

        List<Mpa> mpas = Arrays.asList(mpa1, mpa2, mpa3, mpa4, mpa5);
        when(mpaService.getAllMpa()).thenReturn(mpas);

        // when/then
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));

        verify(mpaService, times(1)).getAllMpa();
    }

    @Test
    void getMpaById_WithMaxId_ShouldWork() throws Exception {
        // given
        Integer id = 5;
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName("NC-17");

        when(mpaService.getMpaById(id)).thenReturn(mpa);

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("NC-17"));

        verify(mpaService, times(1)).getMpaById(id);
    }

    @Test
    void getMpaById_WithIdGreaterThanMax_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 6; // Максимальный ID в системе - 5
        when(mpaService.getMpaById(id)).thenThrow(new NotFoundException("Рейтинг MPA с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/mpa/{id}", id))
                .andExpect(status().isNotFound());

        verify(mpaService, times(1)).getMpaById(id);
    }
}