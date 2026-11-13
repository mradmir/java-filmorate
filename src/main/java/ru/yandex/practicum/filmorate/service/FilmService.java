
package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) {
        validateFilm(film);
        film.setLikes(new java.util.HashSet<>());
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        return filmStorage.update(film);
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);

        film.getLikes().add(userId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество должно быть больше нуля");
        }

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        LocalDate minDate = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Некорректная дата релиза");
        }
    }
}

