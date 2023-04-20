package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        filmStorage.findFilmById(filmId).getLikes().add(userId);
        filmStorage.findFilmById(filmId).setLikesCount(filmStorage.findFilmById(filmId).getLikes().size());
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
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
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
