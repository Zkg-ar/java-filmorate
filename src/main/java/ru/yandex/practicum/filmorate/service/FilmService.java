package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void updateFilm(Film film) {
        filmStorage.updateFilm(film);
    }

    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }


    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id);
    }


    public List getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film putLike(int filmId, int userId) {
        checkIdAvailability(filmId, userId);
        Film film = filmStorage.findFilmById(filmId);
        film.getLikes().add(userId);
        return filmStorage.findFilmById(filmId);
    }

    public void deleteLike(int filmId, int userId) {
        checkIdAvailability(filmId, userId);
        filmStorage.findFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException("Введено отрицательное число");
        }

        Comparator<Film> filmLikesComparator = Comparator.comparingInt(film -> film.getLikes().size());
        return filmStorage.getAllFilms().stream()
                .sorted(filmLikesComparator.reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


    private void checkIdAvailability(int filmId, int userId) {
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Лайк не поставлен.Отсутсвует пользователь с id = " + userId));

        filmStorage.getAllFilms().stream()
                .filter(x -> x.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Отсутсвует фильм с id = " + filmId));
    }

}
