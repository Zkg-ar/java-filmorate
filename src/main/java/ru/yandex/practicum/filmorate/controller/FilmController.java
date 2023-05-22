package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    private final FilmService service;


    @GetMapping("/films")
    public List<Film> getFilms() {
        return service.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getUserById(@PathVariable Integer id) {
        return service.findFilmById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return service.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        return service.getGenreById(id);
    }



    @GetMapping("/mpa")
    public List<MpaRating> getAllMpaRatings() {
        return service.getAllRatings();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getRatingsById(@PathVariable Integer id) {
        return service.getRatingById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        log.info("Новый фильм успешно создан {}", film);
        return service.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);

        log.info("Фильм {} обновлен", film);
        return service.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void userLikesFilm(@PathVariable Integer id,
                              @PathVariable Integer userId) {
        service.putLike(id, userId);
    }


    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {

        service.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return service.getPopularFilms(count);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше чем 28.12.1895");
        }
    }

}
