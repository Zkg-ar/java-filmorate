package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import javax.validation.Valid;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private Map<Integer, Film> films = new HashMap();
    private int id = 0;


    @GetMapping("/films")
    public List getFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Новый пользователь успешно зарегестрирован {}", film);

        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Пользователь не найден.");
        }
        films.put(film.getId(), film);
        log.info("Film {} updated", film);

        return film;
    }

    private int generateId() {
        return ++id;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше чем 28.12.1895");
        }
    }

}
