package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String url = "/users";

    private User createUser() {
        return User.builder()
                .email("yandex@mail.ru")
                .login("secret-login")
                .name("my name")
                .birthday(LocalDate.of(1999, 12, 31))
                .build();

    }

    @Test
    public void createNewUser_IfRequestedPOSTMethod() {
        User user = createUser();
        ResponseEntity<User> response = restTemplate.postForEntity(url, user, User.class);
        Assertions.assertEquals("my name", response.getBody().getName());
    }

    @Test
    public void createNewUser_IfEmptyName() {
        User user = createUser();
        user.setName(null);

        ResponseEntity<User> response = restTemplate.postForEntity(url, user, User.class);
        Assertions.assertEquals("secret-login", response.getBody().getName());
    }

    @Test
    public void throwValidationException_ifEmptyEmail() {
        User user = createUser();
        user.setEmail(null);
        ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifEmptyLogin() {
        User user = createUser();
        user.setLogin("");
        ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifLoginWithSpaces() {
        User user = createUser();
        user.setLogin("my login");
        ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifInvalidBirthDate() {
        User user = createUser();
        ResponseEntity<User> createResponse = restTemplate.postForEntity(url, user, User.class);

        User updatedUser = user;
        updatedUser.setBirthday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        updatedUser.setId(1);
        HttpEntity<User> request = new HttpEntity<>(updatedUser);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void throwNotFoundException_ifUserWithSameIdDoesNotExist() {
        User user = createUser();
        ResponseEntity<User> createResponse = restTemplate.postForEntity(url, user, User.class);

        User updatedUser = user;
        updatedUser.setId(Integer.MAX_VALUE);
        HttpEntity<User> request = new HttpEntity<>(updatedUser);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void throwMethodArgumentNotValidException_ifIncorrectEmail() {
        User user = createUser();
        user.setEmail("my emailyandex.ru");

        ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}
