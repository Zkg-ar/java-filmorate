package ru.yandex.practicum.filmorate.storage.filmStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private int id = 0;


    private int generateId() {
        return ++id;
    }

    @Override
    public void addFilm(Film film) {
        for (Film film1 : films.values()) {
            if (film1.getName().equals(film.getName())) {
                throw new FilmAlreadyExistException("Такой фильм уже существует");
            }
        }

        film.setId(generateId());
        films.put(film.getId(), film);
    }

    @Override
    public Film findFilmById(int id) {
        return films.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id = " + id + " отсутсвует"));
    }


    @Override
    public List getAllFilms() {
        return films.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.values().contains(film)) {
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " отсутсвует");
        }
        films.put(film.getId(), film);
    }
}