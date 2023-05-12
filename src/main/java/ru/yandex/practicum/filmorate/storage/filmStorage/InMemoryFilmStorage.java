package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private List<Genre> genre = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Триллер"),
            new Genre(4, "Ужасы"),
            new Genre(5, "Боевик"),
            new Genre(6, "Детектив")
    );

    private List<MpaRating> mpa = List.of(
            new MpaRating(1, "G"),
            new MpaRating(2, "PG"),
            new MpaRating(3, "PG-13"),
            new MpaRating(4, "R"),
            new MpaRating(5, "NC-17")
    );


    private int id = 0;


    private int generateId() {
        return ++id;
    }

    @Override
    public Film addFilm(Film film) {
        for (Film film1 : films.values()) {
            if (film1.getId() == film.getId()) {
                throw new FilmAlreadyExistException("Такой фильм уже существует");
            }
        }

        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id = " + id + " не существует");
        }
        return films.get(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genre.stream().collect(Collectors.toList());
    }

    @Override
    public MpaRating getRatingById(Integer id) {
        return mpa.get(id);
    }

    @Override
    public List<MpaRating> getAllRatings() {
        return mpa.stream().collect(Collectors.toList());
    }

    @Override
    public Genre getGenreById(Integer id) {
        return genre.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public Film updateFilm(Film film) {
        films.values().stream().filter(x -> x.getId() == film.getId()).findFirst().orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " не существует"));
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException("Введено отрицательное число");
        }

        Comparator<Film> filmLikesComparator = Comparator.comparingInt(film -> film.getLikes().size());
        return (List<Film>) getAllFilms().stream()
                .sorted(filmLikesComparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void putLike(int filmId, int userId) {
        checkIdAvailability(filmId, userId);
        Film film = findFilmById(filmId);
        film.getLikes().add(userId);

    }

    @Override
    public void deleteLike(int filmId, int userId) {
        checkIdAvailability(filmId, userId);
        findFilmById(filmId).getLikes().remove(userId);
    }

    private void checkIdAvailability(int filmId, int userId) {
//        getAllUsers().stream()
//                .filter(x -> x.getId() == userId)
//                .findFirst()
//                .orElseThrow(() -> new UserNotFoundException("Лайк не поставлен.Отсутсвует пользователь с id = " + userId));

        getAllFilms().stream()
                .filter(x -> x.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Отсутсвует фильм с id = " + filmId));
    }
}
