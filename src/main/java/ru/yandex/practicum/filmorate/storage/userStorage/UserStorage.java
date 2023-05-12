package ru.yandex.practicum.filmorate.storage.userStorage;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    User findUserById(int id);

    List<User> getUsersFriends(int id);
    void deleteFriend(int id, int friendId);
    List<User> findCommonFriend(int id, int otherId);
    void addFriends(int id, int friendId);
}
