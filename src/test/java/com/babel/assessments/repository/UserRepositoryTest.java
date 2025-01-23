package com.babel.assessments.repository;

import com.babel.assessments.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryTest {
    private UserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new UserRepository();
        repo.getAll().clear();
    }

    @Test
    void testCreateUser() {
        User user = new User(null, "Test", "1234567", "test@example.com", "");
        User created = repo.createUser(user);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(1, repo.getAll().size());
    }

    @Test
    void testGetById() {
        User user = new User(null, "Test2", "1234567", "test2@example.com", "");
        User created = repo.createUser(user);
        Optional<User> found = repo.getById(created.getId());
        Assertions.assertTrue(found.isPresent());
    }

    @Test
    void testUpdateUser() {
        User u = new User(null, "Original", "9999999", "orig@example.com", "");
        User created = repo.createUser(u);
        User updatedData = new User(null, "Updated", "8888888", "upd@example.com", "admin");
        User updated = repo.updateUser(created.getId(), updatedData);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("Updated", updated.getName());
        Assertions.assertEquals("admin", updated.getRole());
    }

    @Test
    void testDeleteUser() {
        User u = new User(null, "To Delete", "1111111", "del@example.com", "");
        User created = repo.createUser(u);
        boolean result = repo.deleteUser(created.getId());
        Assertions.assertTrue(result);
        Assertions.assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void testDeleteNonExisting() {
        boolean result = repo.deleteUser(UUID.randomUUID());
        Assertions.assertFalse(result);
    }
}
