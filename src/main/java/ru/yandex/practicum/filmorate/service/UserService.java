package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.RequestStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("UserDbStorage")
    private final UserStorage storage;

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User findUserById(int id) {
        return storage.findUserById(id);
    }

    public User addUser(User user) {
        return storage.addUser(user);
    }

    public void updateUser(User user) {
        storage.updateUser(user);
    }


    public void addFriends(int id, int friendId) {
        storage.addFriends(id,friendId);
    }

    public List<User> findCommonFriend(int id, int otherId) {
        return storage.findCommonFriend(id,otherId);
    }


    public List<User> getUsersFriends(int id) {
        return storage.getUsersFriends(id);
    }

    public void deleteFriend(int id, int friendId) {
        storage.deleteFriend(id,friendId);
    }


}
