package com.babel.assessments.routes;

import com.babel.assessments.model.User;
import com.babel.assessments.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

@QuarkusTest
public class UserRouteBuilderTest {
    @Inject
    ProducerTemplate template;

    @Inject
    UserRepository repo;

    @BeforeEach
    void setUp() {
        repo.getAll().clear();
    }

    @Test
    void testCreateUserValid() {
        User u = new User(null, "Alice", "123456789", "alice@example.com", "");
        User result = template.requestBody("direct:createUser", u, User.class);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(1, repo.getAll().size());
    }

    @Test
    void testCreateUserInvalid() {
        User u = new User(null, "BadEmail", "123456789", "invalid_email", "");
        Assertions.assertThrows(CamelExecutionException.class, () -> {
            template.requestBody("direct:createUser", u, User.class);
        });
    }

    @Test
    void testGetByIdNotFound() {
        Assertions.assertThrows(CamelExecutionException.class, () -> {
            template.requestBody("direct:getUserById", UUID.randomUUID(), User.class);
        });
    }

    @Test
    void testUpdateUser() {
        User created = repo.createUser(new User(null, "Bob", "123456789", "bob@example.com", ""));
        User updates = new User(null, "Bob Updated", "987654321", "updated@example.com", "admin");
        User result = template.requestBody("direct:updateUser", new Object[]{created.getId(), updates}, User.class);
        Assertions.assertEquals("Bob Updated", result.getName());
        Assertions.assertEquals("admin", result.getRole());
    }
}
