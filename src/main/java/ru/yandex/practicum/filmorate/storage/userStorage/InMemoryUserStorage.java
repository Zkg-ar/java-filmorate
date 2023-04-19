package ru.yandex.practicum.filmorate.storage.userStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int id = 0;


    private int generateId() {
        return ++id;
    }

    @Override
    public void addUser(User user) {

        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует");
            }
        }

        user.setId(generateId());
        users.put(user.getId(), user);
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
}
