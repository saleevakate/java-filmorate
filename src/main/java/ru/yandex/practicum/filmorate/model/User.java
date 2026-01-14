package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class User {
    private Integer id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть null")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
