package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    Map<Integer, Film> films = new HashMap();
    private int id = 0;


    @GetMapping("/films")
    public Map findAll() {
        return films;
    }

    @PostMapping("/film")
    public Film createFilm(@RequestBody Film film) {
        film.setId(generateId());
        if (validate(film)) {
            films.put(film.getId(), film);
            log.info("Новый пользователь успешно зарегестрирован {}", film);
        }
        return film;
    }

    @PutMapping("/film")
    public Film updateFilm(@RequestBody Film film) {
        if (validate(film)) {
            films.put(film.getId(), film);
            log.info("Данные пользователя {} успешно обнавлены.", film);
        }
        return film;
    }

    private int generateId() {
        return ++id;
    }

    private boolean validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            throw new ValidationException("Дата релиза не может быть раньше чем 28.12.1895");
        } else if (!film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }

}
