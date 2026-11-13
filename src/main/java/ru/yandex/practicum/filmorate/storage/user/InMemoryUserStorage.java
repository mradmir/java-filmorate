
package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(int id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден: " + id);
        }

        return user;
    }

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = findById(user.getId());
        user.setFriends(oldUser.getFriends());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        findById(id);
        users.remove(id);

        for (User user : users.values()) {
            user.getFriends().remove(id);
        }
    }
}

