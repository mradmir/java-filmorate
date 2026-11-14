
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
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
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id=" + id + " не найден"
                ));
    }

    public User create(User user) {
        applyDefaultName(user);
        user.setFriends(new HashSet<>());
        return userStorage.create(user);
    }

    public User update(User user) {
        findById(user.getId());
        applyDefaultName(user);
        return userStorage.update(user);
    }

    public void delete(int id) {
        findById(id);
        userStorage.delete(id);
    }

    public void addFriend(int id, int friendId) {
        checkDifferentUsers(id, friendId);

        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        checkDifferentUsers(id, friendId);

        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(int id) {
        User user = findById(id);

        return user.getFriends().stream()
                .sorted()
                .map(this::findById)
                .toList();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        checkDifferentUsers(id, otherId);

        User user = findById(id);
        User other = findById(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .sorted()
                .map(this::findById)
                .toList();
    }

    private void checkDifferentUsers(int id, int otherId) {
        if (id == otherId) {
            throw new ValidationException(
                    "Пользователи должны быть разными"
            );
        }
    }

    private void applyDefaultName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
