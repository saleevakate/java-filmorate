package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    int id;
    String email;
    String login;
    String name;
    LocalDateTime birthday;
}
