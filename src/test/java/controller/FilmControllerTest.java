package controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.filmStorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;


public class FilmControllerTest {

    private Map<Integer, Film> films;
    Film film;
    FilmController obj;
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        FilmService service = new FilmService(new InMemoryFilmStorage(),new InMemoryUserStorage());
        obj = new FilmController(service);
        films = new HashMap<>();
        film = Film.builder()
                .name("Avengers.Infinity war")
                .description("Фильм снятый по комиксам")
                .duration(120)
                .releaseDate(LocalDate.of(2018, 4, 29))
                .id(1).build();

    }

    @Test
    public void addOneFilmTest() {
        films.put(film.getId(), film);
        assertEquals("Фильм не добавлен", 1, films.size());
    }

    @Test
    public void whenFilmNameIsEmpty() {

        film.setName(null);
        obj.createFilm(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.forEach(action -> {
            assertThat(action.getMessage()).isEqualTo("Название фильма не может быть пустым");
        });
    }

    @Test
    public void whenDescriptionLengthIsMore200() {

        film.setDescription("О".repeat(210));
        obj.createFilm(film);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.forEach(action -> {
            assertThat(action.getMessage()).isEqualTo("Описание фильма не может быть больше 200 символов");
        });


    }

    @Test
    public void whenFilmDurationIsNegative() {

        film.setDuration(-100);
        obj.createFilm(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        violations.forEach(action -> {
            assertThat(action.getMessage()).isEqualTo("Продолжительность фильма должна быть положительной");
        });

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
