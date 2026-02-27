package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private final LocalDate birthDayMovie = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выбран метод GET - получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Получен запрос на создание фильма: {}", film);
        try {
            if (film == null) {
                throw new ValidationException("Тело запроса не может быть пустым");
            }
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Длина описания не должна быть больше 200");
            }
            if (film.getReleaseDate().isBefore(birthDayMovie)) {
                throw new ValidationException("Дата релиза должна быть не раньше  28 декабря 1895 года");
            }
            if (film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
        film.setId(getNextId());
        log.trace("Устанавливаем новое значение поля id {}", film.getId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен - {}", film);
        return film;
    }

    private long getNextId() {
        log.debug("Вызван метод getNextId для получения следующего id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Текущее максимальное значение id - {}", currentMaxId);
        log.debug("Увеличиваем значение id перед использованием текущего значения в выражении");
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) throws ValidationException {
        log.debug("Получен запрос на обновление фильма: {}", newFilm);
        try {
            if (newFilm == null) {
                throw new ValidationException("Тело запроса не может быть пустым");
            }
            if (newFilm.getId() == null) {
                throw new ValidationException("Укажите id обновляемого фильма");
            }
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Длина описания не должна быть больше 200");
            }
            if (newFilm.getReleaseDate().isBefore(birthDayMovie)) {
                throw new ValidationException("Дата релиза должна быть не раньше  28 декабря 1895 года");
            }
            if (newFilm.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
        Film oldFilm = films.get(newFilm.getId());
        log.debug("Создаем объект копию oldFilm обновляемого фильма");
        if (oldFilm == null) {
            log.warn("Фильм с id={} не найден", newFilm.getId());
            throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.debug("Обновление фильма id={}:", oldFilm.getId());
        log.debug("  name: '{}' → '{}'", oldFilm.getName(), newFilm.getName());
        log.debug("  description: '{}' → '{}'",
                oldFilm.getDescription(), newFilm.getDescription());
        log.debug("  releaseDate: {} → {}",
                oldFilm.getReleaseDate(), newFilm.getReleaseDate());
        log.debug("  duration: {} → {}",
                oldFilm.getDuration(), newFilm.getDuration());
        log.info("Фильм успешно обновлен - {}", newFilm);
        return oldFilm;
    }
}
