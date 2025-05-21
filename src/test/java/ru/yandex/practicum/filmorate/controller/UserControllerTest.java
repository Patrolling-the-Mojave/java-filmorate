package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String url = "/users";
    private User testUser;
    private NewUserDto newUser;

    @BeforeEach
    void setUP() {
        jdbcTemplate.update("DELETE FROM users");
        testUser = User.builder()
                .email("yandex@mail.ru")
                .login("secret-login")
                .name("my name")
                .birthday(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .build();

    }

    @Test
    public void createNewUser_IfRequestedPOSTMethod() {
        ResponseEntity<UserDto> response = restTemplate.postForEntity(url, testUser, UserDto.class);
        Assertions.assertEquals("my name", response.getBody().getName());
        Assertions.assertEquals(1, response.getBody().getId());
        System.out.println(response.getBody());
    }

    @Test
    public void createNewUser_IfEmptyName() {
        testUser.setName(null);

        ResponseEntity<UserDto> response = restTemplate.postForEntity(url, testUser, UserDto.class);
        Assertions.assertEquals("secret-login", response.getBody().getName());
    }

    @Test
    public void throwValidationException_ifEmptyEmail() {
        testUser.setEmail(null);
        ResponseEntity<String> response = restTemplate.postForEntity(url, testUser, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifEmptyLogin() {
        testUser.setLogin("");
        ResponseEntity<String> response = restTemplate.postForEntity(url, testUser, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifLoginWithSpaces() {
        testUser.setLogin("my login");
        ResponseEntity<String> response = restTemplate.postForEntity(url, testUser, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifInvalidBirthDate() {
        ResponseEntity<User> createResponse = restTemplate.postForEntity(url, testUser, User.class);

        User updatedUser = testUser;
        updatedUser.setBirthday(Date.valueOf(LocalDate.now().plus(1, ChronoUnit.DAYS)));
        updatedUser.setId(1);
        HttpEntity<User> request = new HttpEntity<>(updatedUser);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwNotFoundException_ifUserWithSameIdDoesNotExist() {
        ResponseEntity<User> createResponse = restTemplate.postForEntity(url, testUser, User.class);

        User updatedUser = testUser;
        updatedUser.setId(Integer.MAX_VALUE);
        HttpEntity<User> request = new HttpEntity<>(updatedUser);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void throwMethodArgumentNotValidException_ifIncorrectEmail() {
        User user = testUser;
        user.setEmail("my emailyandex.ru");

        ResponseEntity<String> response = restTemplate.postForEntity(url, user, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
