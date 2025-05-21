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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String url = "/films";

    private NewFilmDto createValidFilmDto() {
        return NewFilmDto.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(new MpaDto(1))
                .build();
    }

    private UpdateFilmDto createUpdateFilmDto(int id) {
        return UpdateFilmDto.builder()
                .id(id)
                .name("Inception Updated")
                .description("New description")
                .releaseDate(LocalDate.of(2011, 8, 17))
                .duration(150)
                .mpa(new MpaRating(2, "PG"))
                .build();
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM film");
    }

    @Test
    public void getAllFilms_returnCreatedFilms_ifRequestedGETMethod() {
        NewFilmDto film = createValidFilmDto();
        ResponseEntity<FilmDto> createResponse = restTemplate.postForEntity(url, film, FilmDto.class);
        ResponseEntity<FilmDto[]> response = restTemplate.getForEntity(url, FilmDto[].class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().length);
    }

    @Test
    public void createNewFilm_ifRequestedPOSTMethod() {
        NewFilmDto film = createValidFilmDto();
        ResponseEntity<FilmDto> response = restTemplate.postForEntity(url, film, FilmDto.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Inception", response.getBody().getName());
    }

    @Test
    public void throwValidationException_ifEmptyName() {
        NewFilmDto invalidFilm = createValidFilmDto();
        invalidFilm.setName(" ");
        ResponseEntity<String> response = restTemplate.postForEntity(url, invalidFilm, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifInvalidReleaseDate() {
        NewFilmDto invalidFilm = createValidFilmDto();
        invalidFilm.setReleaseDate(LocalDate.of(1700, 3, 3));
        ResponseEntity<String> response = restTemplate.postForEntity(url, invalidFilm, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateFilm_ifRequestedPUTMethod() {
        NewFilmDto film = createValidFilmDto();
        ResponseEntity<FilmDto> createResponse = restTemplate.postForEntity(url, film, FilmDto.class);
        UpdateFilmDto updatedFilm = createUpdateFilmDto(1);
        updatedFilm.setName("Matrix");

        HttpEntity<UpdateFilmDto> request = new HttpEntity<>(updatedFilm);

        ResponseEntity<Film> response = restTemplate.exchange(url, HttpMethod.PUT, request, Film.class);
        Assertions.assertEquals("Matrix", request.getBody().getName());
    }

    @Test
    public void throwNotFoundException_ifTheMovieWithSameIdDoesNotExist() {
        NewFilmDto film = createValidFilmDto();
        ResponseEntity<FilmDto> createResponse = restTemplate.postForEntity(url, film, FilmDto.class);

        UpdateFilmDto updatedFilm = createUpdateFilmDto(9999);
        HttpEntity<UpdateFilmDto> request = new HttpEntity<>(updatedFilm);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void updateOnlyReleaseDate_ifOtherFieldsAreNull() {
        NewFilmDto validFilm = createValidFilmDto();
        ResponseEntity<FilmDto> createResponse = restTemplate.postForEntity(url, validFilm, FilmDto.class);

        UpdateFilmDto film = createUpdateFilmDto(1);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setDescription(null);
        film.setName(null);
        film.setDuration(null);
        film.setMpa(null);

        HttpEntity<UpdateFilmDto> request = new HttpEntity<>(film);
        ResponseEntity<Film> response = restTemplate.exchange(url, HttpMethod.PUT, request, Film.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(film.getReleaseDate(), request.getBody().getReleaseDate());
    }
}
