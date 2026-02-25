package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        log.debug("Получен запрос на создание фильма - {}", user);
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Email не должен быть пустым или содержать только пробелы");
            }
            if (!(user.getEmail().contains(String.valueOf('@')))) {
                throw new ValidationException("Email должен содержать символ - @ ");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday() == null) {
                throw new ValidationException("Дата рождения должна быть указана");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            if (user.getBirthday().isBefore(LocalDate.of(1900, 1, 1))) {
                throw new ValidationException("Дата рождения должна быть не раньше 1900 года");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
        user.setId(getNextId());
        log.trace("Устанавливаем новое значение поля id {}", user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен - {}", user);
        return user;
    }

    private long getNextId() {
        log.debug("Вызван метод getNextId для получения следующего id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Текущее максимальное значение id - {}", currentMaxId);
        log.debug("Увеличиваем значение id перед использованием текущего значения в выражении");
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) throws ValidationException {
        log.debug("Получен запрос на обновление фильма - {}", newUser);
        try {
            if (newUser.getId() == null) {
                throw new ValidationException("ID пользователя должен быть указан");
            }
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                throw new ValidationException("Email не должен быть пустым или содержать только пробелы");
            }
            if (!(newUser.getEmail().contains(String.valueOf('@')))) {
                throw new ValidationException("Email должен содержать символ - @ ");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }
            if (newUser.getBirthday() == null) {
                throw new ValidationException("Дата рождения должна быть указана");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Пользователь с id={} не найден", newUser.getId());
            throw new ValidationException("Пользователь не найден");
        }
        log.debug("Создаем объект копию oldUser обновляемого пользователя");
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        log.debug("Обновление пользователя id={}:", oldUser.getId());
        log.debug("  email: {} → {}", oldUser.getEmail(), newUser.getEmail());
        log.debug("  login: {} → {}", oldUser.getLogin(), newUser.getLogin());
        log.debug("  name: {} → {}", oldUser.getName(), newUser.getName());
        log.debug("  birthday: {} → {}", oldUser.getBirthday(), newUser.getBirthday());
        log.info("Пользователь успешно обновлен - {}", newUser);
        return oldUser;
    }
}