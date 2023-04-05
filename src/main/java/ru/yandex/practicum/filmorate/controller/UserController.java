package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    Map<Integer, User> users = new HashMap();
    private int id = 0;


    @GetMapping("/users")
    public List findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        if (validate(user)) {
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info("Новый пользователь успешно зарегестрирован {}", user);
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (validate(user)) {
            users.put(user.getId(), user);
            log.info("Данные пользователя {} успешно обнавлены.", user);
        }
        return user;
    }

    private int generateId() {
        return ++id;
    }

    private boolean validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта не может быть пустой и должна содержать @");
        } else if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин пустой или содержит в себе пробелы");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

}
