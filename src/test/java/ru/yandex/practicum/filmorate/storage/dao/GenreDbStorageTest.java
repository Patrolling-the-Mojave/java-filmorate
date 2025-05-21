package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class})
public class GenreDbStorageTest {

    @Autowired
    GenreDbStorage genreDbStorage;

    @Test
    void findById_shouldReturnGenreById() {
        Optional<Genre> genre = genreDbStorage.findById(1);
        Assertions.assertEquals(genre.get().getName(), "Комедия");
    }

    @Test
    void findById_ForUndefinedId_shouldReturnOptionalEmpty() {
        Optional<Genre> genre = genreDbStorage.findById(999);
        Assertions.assertTrue(genre.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllGenres() {
        Collection<Genre> genres = genreDbStorage.findAll();
        Assertions.assertEquals(genres.size(), 6);
    }
}
