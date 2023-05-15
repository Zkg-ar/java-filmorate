package controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;


public class UserControllerTest {

    private Map<Integer, User> users;
    User user;
    UserController obj;
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        UserService service = new UserService(new InMemoryUserStorage());
        obj = new UserController(service);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        users = new HashMap<>();
        user = User.builder()
                .id(1)
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

        user.setEmail("");
        obj.createUser(user);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        violations.forEach(action -> {
            assertThat(action.getMessage()).isEqualTo("Почта не может быть пустой и должна содержать @");
        });
    }

    @Test
    public void createUserWhenLoginIsEmpty() {

        user.setLogin("");
        obj.createUser(user);


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
