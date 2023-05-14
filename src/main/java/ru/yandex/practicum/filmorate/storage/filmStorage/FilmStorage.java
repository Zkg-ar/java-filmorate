package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    List<Film> getAllFilms();

    Film updateFilm(Film film);

    Film findFilmById(int id);

    List<Film> getPopularFilms(Integer count);

    void putLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    MpaRating getRatingById(Integer id);

    List<MpaRating> getAllRatings();

}
