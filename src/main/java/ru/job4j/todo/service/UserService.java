package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.persistence.UserDBStore;

import java.util.Optional;

@Service
public class UserService {
    private final UserDBStore userStore;

    public UserService(UserDBStore userStore) {
        this.userStore = userStore;
    }

    public boolean create(final User user) {
        return userStore.create(user);
    }

    public Optional<User> findById(int id) {
        return userStore.findById(id);
    }

    public boolean updateUser(int id, final User user) {
        return userStore.update(id, user);
    }

    public Optional<User> findByNamePassword(final String name, final String password) {
        return userStore.findByNamePassword(name, password);
    }
}