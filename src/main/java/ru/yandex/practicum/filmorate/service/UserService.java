package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userStorage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.List;
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
        checkIdAvailability(id);
        checkIdAvailability(friendId);

        storage.findUserById(id).getFriends().add(friendId);
        storage.findUserById(friendId).getFriends().add(id);
    }

    public List<User> findCommonFriend(int id, int otherId) {
        checkIdAvailability(id);
        checkIdAvailability(otherId);

        List<Integer> commons = storage.findUserById(id)
                .getFriends()
                .stream()
                .filter(storage.findUserById(otherId).getFriends()::contains).collect(Collectors.toList());
        List<User> commonsList = new ArrayList<>();

        for (int i = 0; i < commons.size(); i++) {
            commonsList.add(storage.findUserById(commons.get(i)));
        }
        return commonsList;
    }
//
//    public List<User> findCommonFriend(int id, int otherId) {
//        checkIdAvailability(id);
//        checkIdAvailability(otherId);
//        List<User> commonsList = new ArrayList<>();
//        for (Integer friendId : storage.findUserById(id).getFriends()) {
//            User friendById = storage.findUserById(friendId);
//            commonsList.add(friendById);
//        }
//
//        return commonsList;
//
//    }

    public List<User> getUsersFriends(int id) {

        storage.getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));

        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : storage.findUserById(id).getFriends()) {
            User friendById = storage.findUserById(friendId);
            friendsList.add(friendById);
        }

        return friendsList;
    }

    public void deleteFriend(int id, int friendId) {
        checkIdAvailability(id);
        checkIdAvailability(friendId);

        storage.findUserById(id).getFriends().remove(friendId);
        storage.findUserById(friendId).getFriends().remove(id);


    }

    private void checkIdAvailability(int id) {
        storage.getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }
}
