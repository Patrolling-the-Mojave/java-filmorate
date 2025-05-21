package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Import(MpaRatingDbStorage.class)
public class MpaRatingDbStorageTest {
    @Autowired
    MpaRatingDbStorage ratingDbStorage;

    @Test
    void findById_shouldReturnRatingById() {
        Optional<MpaRating> mpaRating = ratingDbStorage.findById(1);
        Assertions.assertEquals(mpaRating.get().getName(), "G");
    }

    @Test
    void findAll_shouldReturnAllRatings() {
        Collection<MpaRating> ratings = ratingDbStorage.findAll();
        Assertions.assertEquals(ratings.size(), 5);
    }
}
