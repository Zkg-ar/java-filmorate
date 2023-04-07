package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @Email
    @NotBlank(message = "Почта не может быть пустой и должна содержать @")
    private String email;
    @NotNull
    @NotBlank(message = "Логин пустой или содержит в себе пробелы")
    private String login;
    private String name;
    @NotNull(message = "Задайте дату рождения")
    private LocalDate birthday;
}
