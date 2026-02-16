package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Test
    void getAllGenres_ShouldReturnAllGenres() throws Exception {
        // given - создаем жанры с правильными ID и названиями
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");

        Genre genre4 = new Genre();
        genre4.setId(4);
        genre4.setName("Триллер");

        Genre genre5 = new Genre();
        genre5.setId(5);
        genre5.setName("Документальный");

        Genre genre6 = new Genre();
        genre6.setId(6);
        genre6.setName("Боевик");

        List<Genre> genres = Arrays.asList(genre1, genre2, genre3, genre4, genre5, genre6);

        // настраиваем мок
        when(genreService.getAllGenres()).thenReturn(genres);

        // when/then
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Драма"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Мультфильм"))
                .andExpect(jsonPath("$[3].id").value(4))
                .andExpect(jsonPath("$[3].name").value("Триллер"))
                .andExpect(jsonPath("$[4].id").value(5))
                .andExpect(jsonPath("$[4].name").value("Документальный"))
                .andExpect(jsonPath("$[5].id").value(6))
                .andExpect(jsonPath("$[5].name").value("Боевик"));

        verify(genreService, times(1)).getAllGenres();
    }

    @Test
    void getGenreById_WithValidId_ShouldReturnGenre() throws Exception {
        // given
        Integer id = 1;
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("Комедия");

        when(genreService.getGenreById(id)).thenReturn(genre);

        // when/then
        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Комедия"));

        verify(genreService, times(1)).getGenreById(id);
    }

    @Test
    void getGenreById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = 999;
        when(genreService.getGenreById(id)).thenThrow(new NotFoundException("Жанр с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isNotFound());

        verify(genreService, times(1)).getGenreById(id);
    }

    @Test
    void getGenreById_WithNegativeId_ShouldReturnNotFound() throws Exception {
        // given
        Integer id = -1;
        when(genreService.getGenreById(id)).thenThrow(new NotFoundException("Жанр с ID " + id + " не найден"));

        // when/then
        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isNotFound());

        verify(genreService, times(1)).getGenreById(id);
    }
}