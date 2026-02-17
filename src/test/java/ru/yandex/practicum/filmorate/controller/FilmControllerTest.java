package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    // ==================== ТЕСТЫ НА ПОЛУЧЕНИЕ ВСЕХ ФИЛЬМОВ ====================

    @Test
    void findAll_ShouldReturnAllFilms() throws Exception {
        // given
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film2.setDuration(90);

        Collection<Film> films = Arrays.asList(film1, film2);
        when(filmService.findAll()).thenReturn(films);

        // when/then
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Фильм 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Фильм 2"));

        verify(filmService, times(1)).findAll();
    }

    // ==================== ТЕСТЫ НА СОЗДАНИЕ ФИЛЬМА ====================

    @Test
    void create_WithValidFilm_ShouldReturnCreatedFilm() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        Film createdFilm = new Film();
        createdFilm.setId(1);
        createdFilm.setName("Новый фильм");
        createdFilm.setDescription("Описание");
        createdFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        createdFilm.setDuration(120);
        createdFilm.setMpa(mpa);

        when(filmService.create(any(Film.class))).thenReturn(createdFilm);

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый фильм"))
                .andExpect(jsonPath("$.duration").value(120));

        verify(filmService, times(1)).create(any(Film.class));
    }

    @Test
    void create_WithInvalidFilm_ShouldReturnBadRequest() throws Exception {
        // given
        Film invalidFilm = new Film(); // Пустой фильм без обязательных полей

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFilm)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).create(any(Film.class));
    }

    @Test
    void create_WithBlankName_ShouldReturnBadRequest() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);

        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).create(any(Film.class));
    }

    @Test
    void create_WithNullReleaseDate_ShouldReturnBadRequest() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(null);
        film.setDuration(120);
        film.setMpa(mpa);

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).create(any(Film.class));
    }

    @Test
    void create_WithNegativeDuration_ShouldReturnBadRequest() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(-10);
        film.setMpa(mpa);

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).create(any(Film.class));
    }

    @Test
    void create_WithDescriptionTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);

        String longDescription = "a".repeat(201); // 201 символ

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription(longDescription);
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        // when/then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).create(any(Film.class));
    }

    // ==================== ТЕСТЫ НА ОБНОВЛЕНИЕ ФИЛЬМА ====================

    @Test
    void update_WithValidFilm_ShouldReturnUpdatedFilm() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setId(1);
        film.setName("Обновленный фильм");
        film.setDescription("Обновленное описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(150);
        film.setMpa(mpa);

        when(filmService.update(any(Film.class))).thenReturn(film);

        // when/then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Обновленный фильм"))
                .andExpect(jsonPath("$.duration").value(150));

        verify(filmService, times(1)).update(any(Film.class));
    }

    @Test
    void update_WithNonExistingFilm_ShouldReturnNotFound() throws Exception {
        // given
        Mpa mpa = new Mpa();
        mpa.setId(1);

        Film film = new Film();
        film.setId(999);
        film.setName("Несуществующий фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        when(filmService.update(any(Film.class))).thenThrow(new NotFoundException("Фильм с ID 999 не найден"));

        // when/then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound());

        verify(filmService, times(1)).update(any(Film.class));
    }

    // ==================== ТЕСТЫ НА УДАЛЕНИЕ ФИЛЬМА ====================

    @Test
    void remove_WithValidId_ShouldReturnOk() throws Exception {
        // given
        Integer id = 1;
        when(filmService.remove(id)).thenReturn("Фильм удален");

        // when/then
        mockMvc.perform(delete("/films/{id}", id))
                .andExpect(status().isOk());

        verify(filmService, times(1)).remove(id);
    }

    @Test
    void remove_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(filmService.remove(id)).thenThrow(new NotFoundException("Фильм с ID 999 не найден"));

        // when/then
        mockMvc.perform(delete("/films/{id}", id))
                .andExpect(status().isNotFound());

        verify(filmService, times(1)).remove(id);
    }

    // ==================== ТЕСТЫ НА ПОЛУЧЕНИЕ ФИЛЬМА ПО ID ====================

    @Test
    void getFilmById_WithValidId_ShouldReturnFilm() throws Exception {
        // given
        Integer id = 1;
        Film film = new Film();
        film.setId(id);
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120);

        when(filmService.filmById(id)).thenReturn(film);

        // when/then
        mockMvc.perform(get("/films/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Фильм"));

        verify(filmService, times(1)).filmById(id);
    }

    @Test
    void getFilmById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(filmService.filmById(id)).thenThrow(new NotFoundException("Фильм с ID 999 не найден"));

        // when/then
        mockMvc.perform(get("/films/{id}", id))
                .andExpect(status().isNotFound());

        verify(filmService, times(1)).filmById(id);
    }

    // ==================== ТЕСТЫ НА ЛАЙКИ ====================

    @Test
    void addLike_WithValidIds_ShouldReturnOk() throws Exception {
        // given
        Integer filmId = 1;
        Integer userId = 1;
        doNothing().when(filmService).addLike(filmId, userId);

        // when/then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(filmService, times(1)).addLike(filmId, userId);
    }

    @Test
    void addLike_WithInvalidFilmId_ShouldReturnNotFound() throws Exception {
        // given
        Integer filmId = 999;
        Integer userId = 1;
        doThrow(new NotFoundException("Фильм с ID 999 не найден"))
                .when(filmService).addLike(filmId, userId);

        // when/then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(filmService, times(1)).addLike(filmId, userId);
    }

    @Test
    void addLike_WithInvalidUserId_ShouldReturnNotFound() throws Exception {
        // given
        Integer filmId = 1;
        Integer userId = 999;
        doThrow(new NotFoundException("Пользователь с ID 999 не найден"))
                .when(filmService).addLike(filmId, userId);

        // when/then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());

        verify(filmService, times(1)).addLike(filmId, userId);
    }

    @Test
    void removeLike_WithValidIds_ShouldReturnOk() throws Exception {
        // given
        Integer filmId = 1;
        Integer userId = 1;
        doNothing().when(filmService).removeLike(filmId, userId);

        // when/then
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(filmService, times(1)).removeLike(filmId, userId);
    }

    // ==================== ТЕСТЫ НА ПОПУЛЯРНЫЕ ФИЛЬМЫ ====================

    @Test
    void getPopularFilms_WithDefaultCount_ShouldReturnTop10() throws Exception {
        // given
        List<Film> popularFilms = Arrays.asList(new Film(), new Film());
        when(filmService.getTopFilms(10)).thenReturn(popularFilms);

        // when/then
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(filmService, times(1)).getTopFilms(10);
    }

    @Test
    void getPopularFilms_WithCustomCount_ShouldReturnSpecifiedNumber() throws Exception {
        // given
        Integer count = 5;
        List<Film> popularFilms = Arrays.asList(new Film(), new Film());
        when(filmService.getTopFilms(count)).thenReturn(popularFilms);

        // when/then
        mockMvc.perform(get("/films/popular")
                        .param("count", String.valueOf(count)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(filmService, times(1)).getTopFilms(count);
    }
}