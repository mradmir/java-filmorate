package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelValidationTest {
    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Film validFilm() {
        Film f = new Film();
        f.setName("Film");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        return f;
    }

    private User validUser() {
        User u = new User();
        u.setEmail("a@b.ru");
        u.setLogin("login");
        u.setName("Name");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return u;
    }

    @Test
    void validFilmPasses() {
        assertTrue(validator.validate(validFilm()).isEmpty());
    }

    @Test
    void emptyFilmNameFails() {
        Film f = validFilm();
        f.setName(" ");
        assertFalse(validator.validate(f).isEmpty());
    }

    @Test
    void description200Passes201Fails() {
        Film f = validFilm();
        f.setDescription("a".repeat(200));
        assertTrue(validator.validate(f).isEmpty());
        f.setDescription("a".repeat(201));
        assertFalse(validator.validate(f).isEmpty());
    }

    @Test
    void durationZeroAndNegativeFail() {
        Film f = validFilm();
        f.setDuration(0);
        assertFalse(validator.validate(f).isEmpty());
        f.setDuration(-5);
        assertFalse(validator.validate(f).isEmpty());
        f.setDuration(1);
        assertTrue(validator.validate(f).isEmpty());
    }

    @Test
    void validUserPasses() {
        assertTrue(validator.validate(validUser()).isEmpty());
    }

    @Test
    void emailWithoutAtFails() {
        User u = validUser();
        u.setEmail("abc.ru");
        assertFalse(validator.validate(u).isEmpty());
        u.setEmail("");
        assertFalse(validator.validate(u).isEmpty());
    }

    @Test
    void loginWithSpacesOrEmptyFails() {
        User u = validUser();
        u.setLogin("my login");
        assertFalse(validator.validate(u).isEmpty());
        u.setLogin("");
        assertFalse(validator.validate(u).isEmpty());
    }

    @Test
    void birthdayTodayPassesTomorrowFails() {
        User u = validUser();
        u.setBirthday(LocalDate.now());
        assertTrue(validator.validate(u).isEmpty());
        u.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(validator.validate(u).isEmpty());
    }

    @Test
    void releaseDateBoundary() {

        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();

        FilmService filmService = new FilmService(filmStorage, userStorage);

        FilmController controller = new FilmController(filmService);

        Film f = new Film();
        f.setName("Old");
        f.setDuration(10);

        f.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.create(f));

        f.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.create(f));
    }

    @Test
    void nullReleaseDateFails() {
        Film f = validFilm();
        f.setReleaseDate(null);
        assertFalse(validator.validate(f).isEmpty());
    }

    @Test
    void nullBirthdayFails() {
        User u = validUser();
        u.setBirthday(null);
        assertFalse(validator.validate(u).isEmpty());
    }
}

