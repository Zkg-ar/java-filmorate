package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component("FilmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film addFilm(Film film) {
        String query = "INSERT INTO films(name, description, release_date, duration,mpa_rating_id)\n" +
                "VALUES (?,?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());

            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT films.*,mpa_rating.mpa_rating_name FROM films " +
                "INNER JOIN mpa_rating ON films.mpa_rating_id = mpa_rating.mpa_rating_id;";
        List<Film> films = jdbcTemplate.query(query, (rs, rowNum) -> makeFilm(rs));
        films.forEach(film -> film.getGenres().addAll(getFilmsGenreById(film.getId())));
        return films;
    }

    private List<Genre> getFilmsGenreById(Integer id) {
        String query = "SELECT DISTINCT * FROM genre RIGHT JOIN film_genre on genre.genre_id = film_genre.genre_id WHERE film_genre.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(query, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
        return genres;
    }


    @Override
    public Film updateFilm(Film film) {
        int rs = jdbcTemplate.update("UPDATE films SET name = ?,description = ?,release_date = ?,duration = ?,mpa_rating_id = ? "
                        + "WHERE id = ?;", film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id,genre_id) VALUES(?,?)", film.getId(), genre.getId());
            }
        }
        if (rs == 0) {
            throw new FilmNotFoundException("Фильм с id = " + film.getId() + " не найден.");
        }
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT films.*,mpa_rating.mpa_rating_name from films,mpa_rating where films.id = ? AND films.mpa_rating_id = mpa_rating.mpa_rating_id", id);

        if (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name")))
                    .genres(new ArrayList<>())
                    .build();
            film.getGenres().addAll(getFilmsGenreById(film.getId()));
            return film;
        } else {
            throw new FilmNotFoundException("Фильс с id = " + id + " не найден.");
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String query = "SELECT f.*, m.mpa_rating_name\n" +
                "                        FROM films AS f\n" +
                "                        INNER JOIN mpa_rating AS m ON f.mpa_rating_id = m.mpa_rating_id\n" +
                "                        LEFT OUTER JOIN likes AS l ON f.id = l.film_id\n" +
                "                        GROUP BY f.id, l.user_id\n" +
                "                        ORDER BY COUNT(l.user_id) DESC\n" +
                "                        LIMIT ?";

        List<Film> popularFilms = jdbcTemplate.query(query, (rs, rowNum) -> makeFilm(rs), count);

        return popularFilms;
    }

    @Override
    public void putLike(int filmId, int userId) {
        String query = "INSERT INTO likes(film_id,user_id) VALUES(?,?);";
        jdbcTemplate.update(query, filmId, userId);
        findFilmById(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Отсутсвует пользователь с id = " + userId);
        }
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(query, filmId, userId);
        findFilmById(filmId).deleteLike(userId);
    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM genre";
        List<Genre> genre = jdbcTemplate.query(query, (rs, rowNum) -> makeGenre(rs));
        return genre;
    }

    @Override
    public List<MpaRating> getAllRatings() {
        String query = "SELECT   * FROM mpa_rating";
        List<MpaRating> mpa = jdbcTemplate.query(query, (rs, rowNum) -> new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name")));
        return mpa;
    }

    @Override
    public MpaRating getRatingById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT   * FROM mpa_rating WHERE mpa_rating_id = ?", id);
        if (rs.next()) {
            MpaRating mpaRating = MpaRating.builder()
                    .id(rs.getInt("mpa_rating_id"))
                    .name(rs.getString("mpa_rating_name"))
                    .build();
            return mpaRating;
        } else {
            throw new NotFoundException("Отсутсвует рейтинг с id = " + id);
        }
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT   * FROM genre WHERE genre_id = ?;", id);
        if (rs.next()) {
            Genre genre = Genre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("name"))
                    .build();
            return genre;
        } else {
            throw new NotFoundException("Отсутсвует жанр с id = " + id);
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRating(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name")))
                .genres(new ArrayList<>())
                .build();

    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }


}
