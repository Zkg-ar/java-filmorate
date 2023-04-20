package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List getUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return service.findUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUsersFriends(@PathVariable Integer id) {
        return service.getUsersFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id,
                                      @PathVariable Integer otherId) {
        return service.findCommonFriend(id, otherId);
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        service.addUser(user);
        log.info("Новый пользователь успешно зарегестрирован {}", user);
        return user;
    }


    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        service.updateUser(user);
        log.info("Пользователь {} обновлен", user);
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Optional<Integer> id,
                           @PathVariable Optional<Integer> friendId) {
        service.addFriends(id.get(), friendId.get());
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        service.deleteFriend(id, friendId);
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
