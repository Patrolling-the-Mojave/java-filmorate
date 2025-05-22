package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;

@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({FilmDbStorage.class,
        GenreDbStorage.class,
        UserDbStorage.class,
        MpaRatingDbStorage.class,
        FilmMapper.class,
        UserMapper.class

})
public class FilmDbStorageTest {

    @Autowired
    FilmDbStorage filmDbStorage;

    @Autowired
    UserDbStorage userDbStorage;

    NewFilmDto newFilm;
    NewUserDto newUserDto;

    @BeforeEach
    void setUp() {
        newUserDto = new NewUserDto();
        newUserDto.setName("Alex");
        newUserDto.setLogin("12345");
        newUserDto.setBirthday(Date.valueOf(LocalDate.now().minus(20, ChronoUnit.YEARS)));
        newUserDto.setEmail("myEmail@gmail.com");

        newFilm = NewFilmDto.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets...")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(new MpaDto(3))
                .build();
    }

    @Test
    void findById_shouldReturnOptionalFilm() {
        filmDbStorage.save(newFilm);

        Optional<Film> film = filmDbStorage.findById(1);

        Assertions.assertTrue(film.isPresent());
        Assertions.assertEquals(film.get().getName(), newFilm.getName());
    }

    @Test
    void findAll_shouldReturnAllCreatedFilms() {
        filmDbStorage.save(newFilm);
        NewFilmDto secondFilm = newFilm;
        secondFilm.setName("secondFilm");
        filmDbStorage.save(secondFilm);

        Collection<Film> films = filmDbStorage.findAll();

        Assertions.assertEquals(films.size(), 2);
    }

    @Test
    void delete_shouldDeleteFilm() {
        filmDbStorage.save(newFilm);
        NewFilmDto secondFilm = newFilm;
        secondFilm.setName("secondFilm");
        filmDbStorage.save(secondFilm);
        filmDbStorage.delete(1);

        Optional<Film> film = filmDbStorage.findById(1);

        Assertions.assertTrue(film.isEmpty());
    }

    @Test
    void addLike() {
        filmDbStorage.save(newFilm);
        userDbStorage.save(newUserDto);
        filmDbStorage.addLike(1, 1);

        Optional<Film> film = filmDbStorage.findById(1);

        Assertions.assertEquals(film.get().getLikes().size(), 1);
        Assertions.assertTrue(film.get().getLikes().contains(1));
    }
}
