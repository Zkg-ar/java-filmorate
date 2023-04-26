package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void addUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    User findUserById(int id);
}
