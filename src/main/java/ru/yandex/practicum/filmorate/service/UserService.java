
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setFriends(new java.util.HashSet<>());
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (id == friendId) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(int id) {
        User user = userStorage.findById(id);

        return user.getFriends().stream()
                .sorted()
                .map(userStorage::findById)
                .toList();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.findById(id);
        User other = userStorage.findById(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .sorted()
                .map(userStorage::findById)
                .toList();
    }
}

