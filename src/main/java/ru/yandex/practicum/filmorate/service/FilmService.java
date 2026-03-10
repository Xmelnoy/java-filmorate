package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private static final String VALIDATION_ERROR = "Ошибка валидации: ";
    private static final String FILM_NOT_FOUND = "Фильм с id {} не найден";
    private static final String FILM_NOT_FOUND_MESSAGE = "Фильм с id ";
    private static final String USER_NOT_FOUND = "Пользователь с id {} не найден";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        log.debug("Создание фильма с названием: {}", film.getName());
        validateFilm(film);
        Film createdFilm = filmStorage.addFilm(film);
        log.debug("Фильм успешно создан с id: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма с id: {}", film.getId());
        validateFilm(film);

        if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
            log.warn("Обновляем несуществующий фильм с id: {}", film.getId());
            throw new NotFoundException("Фильм с указанным id не найден");
        }

        Film updatedFilm = filmStorage.updateFilm(film);
        log.debug("Фильм с id {} успешно обновлен", updatedFilm.getId());
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn(FILM_NOT_FOUND, id);
            throw new NotFoundException(FILM_NOT_FOUND_MESSAGE + id + " не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        log.debug("Получаем список всех фильмов");
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Пользователь {} ставит лайк фильму {}", userId, filmId);

        Film film = getFilmById(filmId);
        User user = getUserById(userId);

        film.getLikes().add(user.getId());
        log.debug("Лайк успешно добавлен: пользователь {} -> фильм {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Пользователь {} удаляет лайк у фильма {}", userId, filmId);

        Film film = getFilmById(filmId);
        User user = getUserById(userId);

        film.getLikes().remove(user.getId());
        log.debug("Лайк успешно удалён: пользователь {} -> фильм {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Получаем список {} популярных фильмов", count);

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn(USER_NOT_FOUND, id);
            throw new NotFoundException(USER_NOT_FOUND_MESSAGE + id + " не найден");
        }
        return user;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error(VALIDATION_ERROR + "название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error(VALIDATION_ERROR + "максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.error(VALIDATION_ERROR + "дата релиза не может быть пустой");
            throw new ValidationException("Дата релиза не может быть пустой");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error(VALIDATION_ERROR + "дата релиза не может быть раньше 28.12.1895");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }

        if (film.getDuration() <= 0) {
            log.error(VALIDATION_ERROR + "продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}