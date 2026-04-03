package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private static final String VALIDATION_ERROR = "Ошибка валидации: ";
    private static final String USER_NOT_FOUND = "Пользователь с id {} не найден";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id ";

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.debug("Создание пользователя с логином: {}", user.getLogin());
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.addUser(user);
        log.debug("Пользователь успешно создан с id: {}", createdUser.getId());
        return createdUser;
    }

    public User updateUser(User user) {
        log.debug("Обновление пользователя с id: {}", user.getId());
        validateUser(user);

        if (user.getId() == null) {
            log.warn("Обновляем пользователя без id");
            throw new NotFoundException("Пользователь с указанным id не найден");
        }

        userStorage.getUserById(user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        User updatedUser = userStorage.updateUser(user);
        log.debug("Пользователь с id {} успешно обновлен", updatedUser.getId());
        return updatedUser;
    }

    public User getUserById(Long id) {
        log.debug("Получение пользователя с id: {}", id);
        return userStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        log.debug("Получаем список всех пользователей");
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            log.warn("Пользователь {} попытался добавить себя в друзья", userId);
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        log.debug("Добавляем в друзья: пользователь {} добавляет пользователя {}", userId, friendId);

        getUserById(userId);
        getUserById(friendId);

        userStorage.addFriend(userId, friendId);
        log.debug("Пользователь {} успешно добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаляем из друзей: пользователь {} удаляет пользователя {}", userId, friendId);

        getUserById(userId);
        getUserById(friendId);

        userStorage.removeFriend(userId, friendId);
        log.debug("Пользователь {} успешно удалил из друзей пользователя {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.debug("Получаем список друзей пользователя с id: {}", userId);
        User user = getUserById(userId);

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(getUserById(friendId));
        }

        log.debug("У пользователя {} найдено {} друзей", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Поиск общих друзей пользователей {} и {}", userId, otherId);

        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();

        Set<Long> commonFriendIds = userFriends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendIds) {
            commonFriends.add(getUserById(friendId));
        }

        log.debug("У пользователей {} и {} найдено {} общих друзей", userId, otherId, commonFriends.size());
        return commonFriends;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error(VALIDATION_ERROR + "email не может быть пустым и должен содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error(VALIDATION_ERROR + "логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday() == null) {
            log.error(VALIDATION_ERROR + "дата рождения не может быть пустой");
            throw new ValidationException("Дата рождения не может быть пустой");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error(VALIDATION_ERROR + "дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}