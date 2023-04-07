package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private Map<Integer, User> users = new HashMap();
    private int id = 0;


    @GetMapping("/users")
    public List getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно зарегестрирован {}", user);

        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не найден.");
        }
        users.put(user.getId(), user);
        log.info("User {} updated", user);

        return user;
    }

    private int generateId() {
        return ++id;
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

}
