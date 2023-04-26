package ru.yandex.practicum.filmorate.storage.filmStorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void addFilm(Film film);

    List<Film> getAllFilms();

    void updateFilm(Film film);

    Film findFilmById(int id);

}
