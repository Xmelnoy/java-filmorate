package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private static final String VALIDATION_ERROR = "Ошибка валидации: ";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film createFilm(Film film) {
        log.debug("Создание фильма с названием: {}", film.getName());
        validateFilm(film);
        validateMpaAndGenres(film);

        Film createdFilm = filmStorage.addFilm(film);
        log.debug("Фильм успешно создан с id: {}", createdFilm.getId());
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма с id: {}", film.getId());
        validateFilm(film);

        if (film.getId() == null) {
            log.warn("Попытка обновить фильм без id");
            throw new NotFoundException("Фильм с указанным id не найден");
        }

        filmStorage.getFilmById(film.getId());
        validateMpaAndGenres(film);

        Film updatedFilm = filmStorage.updateFilm(film);
        log.debug("Фильм с id {} успешно обновлен", updatedFilm.getId());
        return updatedFilm;
    }

    public Film getFilmById(Long id) {
        log.debug("Получение фильма с id: {}", id);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getAllFilms() {
        log.debug("Получаем список всех фильмов");
        return filmStorage.getAllFilms();
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Пользователь {} ставит лайк фильму {}", userId, filmId);

        getFilmById(filmId);
        getUserById(userId);

        filmStorage.addLike(filmId, userId);
        log.debug("Лайк успешно добавлен: пользователь {} -> фильм {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Пользователь {} удаляет лайк у фильма {}", userId, filmId);

        getFilmById(filmId);
        getUserById(userId);

        filmStorage.removeLike(filmId, userId);
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
        log.debug("Получение пользователя с id: {}", id);
        return userStorage.getUserById(id);
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

        if (film.getMpa() == null || film.getMpa().getId() == null) {
            log.error(VALIDATION_ERROR + "рейтинг MPA должен быть указан");
            throw new ValidationException("Рейтинг MPA должен быть указан");
        }
    }

    private void validateMpaAndGenres(Film film) {
        Integer mpaId = film.getMpa().getId();
        log.debug("Проверяем существование рейтинга MPA с id: {}", mpaId);
        mpaStorage.getMpaById(mpaId);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre == null || genre.getId() == null) {
                    log.error(VALIDATION_ERROR + "жанр фильма указан некорректно");
                    throw new ValidationException("Жанр фильма указан некорректно");
                }

                log.debug("Проверяем существование жанра с id: {}", genre.getId());
                genreStorage.getGenreById(genre.getId());
            }
        }
    }
}