package ru.yandex.practicum.filmorate.storage.userStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.RequestStatus;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int id = 0;


    private int generateId() {
        return ++id;
    }

    @Override
    public User addUser(User user) {

        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует");
            }
        }

        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(int id) {
        return users.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " отсутсвует"));
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        users.put(user.getId(), user);
    }

    @Override
    public List<User> getAllUsers() {
        return users.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersFriends(int id) {
        findUserById(id);
        List<User> friendsList = new ArrayList<>();
        for (Integer friendId : findUserById(id).getFriends().keySet()) {
            User friendById = findUserById(friendId);
            friendsList.add(friendById);
        }

        return friendsList;
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        checkIdAvailability(id);
        checkIdAvailability(friendId);

        findUserById(id).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(id);
    }

    public void addFriends(int id, int friendId) {
        checkIdAvailability(id);
        checkIdAvailability(friendId);

        findUserById(id).getFriends().put(friendId, RequestStatus.Confirmed);
        findUserById(friendId).getFriends().put(id, RequestStatus.Confirmed);
    }

    public List<User> findCommonFriend(int id, int otherId) {
        checkIdAvailability(id);
        checkIdAvailability(otherId);

        List<Integer> commons = findUserById(id)
                .getFriends()
                .keySet()
                .stream()
                .filter(findUserById(otherId).getFriends().keySet()::contains).collect(Collectors.toList());
        List<User> commonsList = new ArrayList<>();

        for (int i = 0; i < commons.size(); i++) {
            commonsList.add(findUserById(commons.get(i)));
        }
        return commonsList;
    }

    private void checkIdAvailability(int id) {
        getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }
}
