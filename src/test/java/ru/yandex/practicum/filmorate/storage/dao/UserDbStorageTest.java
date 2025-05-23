package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;


@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({UserDbStorage.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private NewUserDto newUserDto;
    private NewUserDto secondNewUser;

    @BeforeEach
    void setUp() {
        newUserDto = new NewUserDto();
        newUserDto.setName("Alex");
        newUserDto.setLogin("12345");
        newUserDto.setBirthday(Date.valueOf(LocalDate.now().minus(20, ChronoUnit.YEARS)));
        newUserDto.setEmail("myEmail@gmail.com");

        secondNewUser = new NewUserDto();
        secondNewUser.setName("Alex");
        secondNewUser.setLogin("123456");
        secondNewUser.setBirthday(Date.valueOf(LocalDate.now().minus(20, ChronoUnit.YEARS)));
        secondNewUser.setEmail("myEmail@yandex.com");
    }

    @Test
    void findById_ShouldReturnSavedUser() {
        userDbStorage.save(newUserDto);

        Optional<User> user = userDbStorage.findById(1);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(user.get().getName(), "Alex");
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userDbStorage.save(newUserDto);
        userDbStorage.save(secondNewUser);

        Collection<User> users = userDbStorage.findAll();

        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        userDbStorage.save(secondNewUser);
        userDbStorage.save(newUserDto);
        userDbStorage.delete(1);

        Collection<User> users = userDbStorage.findAll();

        Assertions.assertEquals(users.size(), 1);
    }

    @Test
    void findById_forUndefinedId_shouldReturnOptionalEmpty() {
        userDbStorage.save(newUserDto);
        Optional<User> user = userDbStorage.findById(999);

        Assertions.assertTrue(user.isEmpty());
    }

    @Test
    void addFriend_shouldCreateNewFriendship() {
        userDbStorage.save(newUserDto);
        userDbStorage.save(secondNewUser);
        userDbStorage.addFriend(1, 2);

        User user = userDbStorage.findById(1).get();
        User friend = userDbStorage.findById(2).get();

        Assertions.assertTrue(!user.getFriends().isEmpty());
        Assertions.assertTrue(friend.getFriends().isEmpty());
    }

    @Test
    void deleteFriend_shouldDeleteFriendship() {
        userDbStorage.save(newUserDto);
        userDbStorage.save(secondNewUser);
        userDbStorage.addFriend(1, 2);

        User user = userDbStorage.findById(1).get();
        User friend = userDbStorage.findById(2).get();

        Assertions.assertTrue(!user.getFriends().isEmpty());
        Assertions.assertTrue(friend.getFriends().isEmpty());

        userDbStorage.deleteFriend(1, 2);

        User updatedUser = userDbStorage.findById(1).get();
        User updatedFriend = userDbStorage.findById(2).get();

        Assertions.assertTrue(updatedFriend.getFriends().isEmpty());
        Assertions.assertTrue(updatedUser.getFriends().isEmpty());
    }

}
