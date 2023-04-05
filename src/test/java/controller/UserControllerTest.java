package controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;


public class UserControllerTest {

    private Map<Integer, User> users;
    User user;
    UserController obj;

    @BeforeEach
    public void beforeEach() {
        obj = new UserController();
        users = new HashMap<>();
        user = User.builder()
                .email("kzg@yan.ru")
                .login("kzg")
                .name("Завен")
                .birthday(LocalDate.of(1999, 04, 29))
                .build();
    }


    @Test
    public void addOneUserTest() {
        users.put(user.getId(), user);
        assertEquals("Пользователь не добавлен", 1, users.size());
    }

    @Test
    public void createUserWhenEmailIsEmpty() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setEmail("");
                    obj.createUser(user);
                });
        Assertions.assertEquals("Почта не может быть пустой и должна содержать @", exception.getMessage());
    }

    @Test
    public void createUserWhenLoginIsEmpty() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin("");
                    obj.createUser(user);
                });
        Assertions.assertEquals("Логин пустой или содержит в себе пробелы", exception.getMessage());
    }

    @Test
    public void createUserWhenNameIsEmpty() {
        user.setName("");
        obj.createUser(user);
        assertEquals("Пользователь не добавлен", user.getLogin(), user.getName());
    }

    @Test
    public void createUserWhenBirthdayIsInFuture() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setBirthday(LocalDate.of(2030, 12, 12));
                    obj.createUser(user);
                });
        Assertions.assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());

    }


}
