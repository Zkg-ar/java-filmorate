package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage storage;

    @Autowired
    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User findUserById(int id) {
        return storage.findUserById(id);
    }

    public void addUser(User user) {
        storage.addUser(user);
    }

    public void updateUser(User user) {
        storage.updateUser(user);
    }


    public void addFriends(int id, int friendId) {
        checkIdAvailability(id, friendId);

        storage.findUserById(id).setFriends(new HashSet<>());
        storage.findUserById(id).getFriends().add((long) friendId);
        storage.findUserById(friendId).setFriends(new HashSet<>());
        storage.findUserById(friendId).getFriends().add((long) id);
    }

    public Set<Long> findCommonFriend(int id, int otherId) {
        checkIdAvailability(id, otherId);
        return storage.findUserById(id)
                .getFriends()
                .stream()
                .filter(storage.findUserById(otherId).getFriends()::contains).collect(Collectors.toSet());
    }

    public Set<Long> getUsersFriends(int id) {
        storage.getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));

        return storage.findUserById(id).getFriends();
    }

    public void deleteFriend(int id, int friendId) {
        checkIdAvailability(id, friendId);

        storage.findUserById(id).getFriends().remove(friendId);
        storage.findUserById(friendId).getFriends().remove(id);


    }

    private void checkIdAvailability(int firstId, int secondId) {
        storage.getAllUsers().stream()
                .filter(x -> x.getId() == firstId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + firstId + " не найден"));
        storage.getAllUsers().stream()
                .filter(x -> x.getId() == secondId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + secondId + " не найден"));
    }
}
