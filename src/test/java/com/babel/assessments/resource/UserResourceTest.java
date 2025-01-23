package com.babel.assessments.resource;

import com.babel.assessments.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class UserResourceTest {

    @Test
    void testCreateUserValid() throws IOException {
        String body = Files.readString(Paths.get("src/test/resources/mock/createUser_valid.json"));
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("role", is("client"));
    }

    @Test
    void testCreateUserInvalid() throws IOException {
        String body = Files.readString(Paths.get("src/test/resources/mock/createUser_invalid.json"));
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/users")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetAllUsers() {
        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetNonExistentUser() {
        given()
                .when()
                .get("/api/users/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateUser() throws IOException {
        String createBody = Files.readString(Paths.get("src/test/resources/mock/createUser_valid.json"));
        User created = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);

        String updateBody = Files.readString(Paths.get("src/test/resources/mock/updateUser_valid.json"));
        User toUpdate = new User(created.getId(), "Updated Name", "987654321", "update@example.com", "admin");
        given()
                .contentType(ContentType.JSON)
                .body(toUpdate)
                .when()
                .put("/api/users/" + created.getId())
                .then()
                .statusCode(200)
                .body("name", is("Updated Name"))
                .body("role", is("admin"));
    }

    @Test
    void testDeleteUser() throws IOException {
        String body = Files.readString(Paths.get("src/test/resources/mock/createUser_valid.json"));
        User created = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(User.class);

        given()
                .when()
                .delete("/api/users/" + created.getId())
                .then()
                .statusCode(204);
    }

    @Test
    void testCsvReport() {
        given()
                .when()
                .get("/api/users/report")
                .then()
                .statusCode(200)
                .contentType("text/csv");
    }
}
