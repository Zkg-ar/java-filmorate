package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }


    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id);
    }


    public List getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void putLike(int filmId, int userId) {
        filmStorage.putLike(filmId, userId);
    }


    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public List<MpaRating> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    public MpaRating getRatingById(int id) {
        return filmStorage.getRatingById(id);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

}
