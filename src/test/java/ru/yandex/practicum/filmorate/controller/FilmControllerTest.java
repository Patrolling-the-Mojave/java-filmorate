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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String url = "/films";

    private Film createValidFilm() {
        return Film.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();

    }

    private Film createAllNullFilm() {
        return Film.builder()
                .releaseDate(null)
                .name(null)
                .description(null)
                .duration(null)
                .build();
    }

    @Test
    public void getAllFilms_returnCreatedFilms_ifRequestedGETMethod() {
        Film film = createValidFilm();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity(url, film, Film.class);
        ResponseEntity<Film[]> response = restTemplate.getForEntity(url, Film[].class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().length);
    }

    @Test
    public void createNewFilm_ifRequestedPOSTMethod() {
        Film film = createValidFilm();
        ResponseEntity<Film> response = restTemplate.postForEntity(url, film, Film.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Inception", response.getBody().getName());
    }

    @Test
    public void throwValidationException_ifEmptyName() {
        Film invalidFilm = createValidFilm();
        invalidFilm.setName(" ");
        ResponseEntity<String> response = restTemplate.postForEntity(url, invalidFilm, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void throwValidationException_ifInvalidReleaseDate() {
        Film invalidFilm = createValidFilm();
        invalidFilm.setReleaseDate(LocalDate.of(1700, 3, 3));
        ResponseEntity<String> response = restTemplate.postForEntity(url, invalidFilm, String.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateFilm_ifRequestedPUTMethod() {
        Film film = createValidFilm();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity(url, film, Film.class);
        Film updatedFilm = film;

        updatedFilm.setName("Matrix");
        updatedFilm.setId(1);
        HttpEntity<Film> request = new HttpEntity<>(updatedFilm);

        ResponseEntity<Film> response = restTemplate.exchange(url, HttpMethod.PUT, request, Film.class);
        Assertions.assertEquals("Matrix", request.getBody().getName());
    }

    @Test
    public void throwNotFoundException_ifTheMovieWithSameIdDoesNotExist() {
        Film film = createValidFilm();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity(url, film, Film.class);

        Film updatedFilm = film;
        updatedFilm.setId(9999);
        HttpEntity<Film> request = new HttpEntity<>(updatedFilm);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }

    @Test
    public void updateOnlyReleaseDate_ifOtherFieldsAreNull() {
        Film validFilm = createValidFilm();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity(url, validFilm, Film.class);

        Film film = createAllNullFilm();
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setId(1);

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity<Film> response = restTemplate.exchange(url, HttpMethod.PUT, request, Film.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(film.getReleaseDate(), request.getBody().getReleaseDate());
    }


}
