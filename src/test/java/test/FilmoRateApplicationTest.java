package test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private User user;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        user = User.builder().id(2).email("kzg@yan.ru").login("kzg").name("Завен").birthday(LocalDate.of(1999, 04, 29)).build();
        film = Film.builder().id(1).name("Avengers.Infinity war").description("Фильм снятый по комиксам").releaseDate(LocalDate.of(2018, 4, 29)).duration(120).mpa(new MpaRating(1, "G")).build();
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(1));

        assertThat(userOptional).isPresent().hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void testGetAllUsers() {
        userStorage.addUser(user);

        List<User> users = userStorage.getAllUsers();
        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.addFilm(film);

        List<Film> films = filmStorage.getAllFilms();
        Assertions.assertEquals(films.size(), 2);
    }

    @Test
    public void testPutLike() {
        filmStorage.putLike(1, 1);
        Assertions.assertEquals(filmStorage.getPopularFilms(1).size(), 1);
    }

//    @Test
//    public void getGenres(){
//        Assertions.assertTrue(filmStorage.getAllGenres().contains(Genre.Комедия));
//    }

    @Test
    public void getGenreById() {
        Assertions.assertTrue(filmStorage.getGenreById(1).equals(new Genre(1, "Комедия")));
    }
}