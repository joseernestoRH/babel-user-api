package com.babel.assessments.repository;

import com.babel.assessments.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository {
    private static final List<User> USERS = new ArrayList<>();

    public List<User> getAll() {
        return USERS;
    }

    public Optional<User> getById(UUID id) {
        return USERS.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public User createUser(User user) {
        user.setId(UUID.randomUUID());
        USERS.add(user);
        return user;
    }

    public User updateUser(UUID id, User data) {
        Optional<User> found = getById(id);
        if (found.isPresent()) {
            User existing = found.get();
            existing.setName(data.getName());
            existing.setWhatsapp(data.getWhatsapp());
            existing.setEmail(data.getEmail());
            existing.setRole(data.getRole());
            return existing;
        }
        return null;
    }

    public boolean deleteUser(UUID id) {
        return USERS.removeIf(u -> u.getId().equals(id));
    }
}
