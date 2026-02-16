package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность не может быть null")
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();

    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();
}