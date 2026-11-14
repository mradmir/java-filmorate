
package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());

        if (oldUser != null) {
            user.setFriends(oldUser.getFriends());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        users.remove(id);

        for (User user : users.values()) {
            user.getFriends().remove(id);
        }
    }
}
