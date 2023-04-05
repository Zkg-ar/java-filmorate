package controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;


public class FilmControllerTest {

    private Map<Integer, Film> films;
    Film film;
    FilmController obj;

    @BeforeEach
    public void beforeEach() {
        obj = new FilmController();
        films = new HashMap<>();
        film = Film.builder()
                .name("Avengers.Infinity war")
                .description("Фильм снятый по комиксам")
                .duration(120)
                .releaseDate(LocalDate.of(2018, 4, 29))
                .build();
    }

    @Test
    public void addOneFilmTest() {
        films.put(film.getId(), film);
        assertEquals("Фильм не добавлен", 1, films.size());
    }

    @Test
    public void whenFilmNameIsEmpty() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setName(null);
                    obj.createFilm(film);
                });
        Assertions.assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    public void whenFilmDurationIsNegative() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setDuration(-100);
                    obj.createFilm(film);
                });
        Assertions.assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }


    @Test
    public void whenFilmReleaseFateIsBefore28121895() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setReleaseDate(LocalDate.of(1800, 4, 29));
                    obj.createFilm(film);
                });
        Assertions.assertEquals("Дата релиза не может быть раньше чем 28.12.1895", exception.getMessage());
    }

}
