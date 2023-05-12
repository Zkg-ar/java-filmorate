package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("UserDbStorage")
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String query = "INSERT INTO users(email,login,name,birthday) values(?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }


    @Override
    public void updateUser(User user) {
        int rs = jdbcTemplate.update("UPDATE users SET login = ?,name = ?,email = ?,birthday = ? " +
                "WHERE id = ?;", user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        if(rs == 0){
            throw new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users;";
        List<User> users = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs));
        return users;
    }

    @Override
    public User findUserById(int id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);

        if (rs.next()) {
            return User.builder()
                    .id(rs.getInt("id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .name(rs.getString("name"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .build();
        } else {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public List<User> getUsersFriends(int id) {
        String query = "SELECT u2.* FROM users AS u1\n" +
                "    INNER JOIN friends f ON u1.id = f.user_id\n" +
                "    INNER JOIN users AS u2 ON f.other_user_id = u2.id\n" +
                "WHERE u1.id = ? AND f.is_confirm = true;";

        List<User> friends = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs), id);
        return friends;
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String query = "DELETE FROM friends WHERE user_id = ? AND other_user_id = ? ;";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public List<User> findCommonFriend(int id, int otherId) {
//        String query = "SELECT * from users WHERE id IN (SELECT other_user_id FROM friends INNER JOIN users u on ? = friends.user_id\n" +
//                "                                                                   INNER JOIN users u2 on ? = friends.user_id );";
        String query = "SELECT u.* FROM friends f\n" +
                "                JOIN users u ON f.other_user_id = u.id\n" +
                "                WHERE f.user_id = ? OR f.user_id = ? AND is_confirm = true\n" +
                "                GROUP BY f.other_user_id\n" +
                "                HAVING COUNT(f.user_id) > 1;";
        List<User> users = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs), id, otherId);
        System.out.println(users);
        return users;
    }

    @Override
    public void addFriends(int id, int friendId) {
        if (id < 0 || friendId < 0) {
            throw new UserNotFoundException("Один из заданых вами id отрицательный");
        }
        String query = "INSERT INTO FRIENDS(user_id, other_user_id, is_confirm) VALUES (?,?,TRUE);";
        jdbcTemplate.update(query, id, friendId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
