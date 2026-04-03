package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmorateApplicationTests {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    private Film validFilm;
    private User validUser;

    @BeforeEach
    void setUp() {
        validFilm = new Film();
        validFilm.setName("Тестовый фильм");
        validFilm.setDescription("Описание");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        validFilm.setMpa(mpa);

        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Тест Тестов");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createFilm_ValidFilm_Success() {
        Film created = filmController.create(validFilm);
        assertNotNull(created.getId());
        assertEquals("Тестовый фильм", created.getName());
    }

    @Test
    void createFilm_NameIsNull_ThrowsException() {
        validFilm.setName(null);
        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_NameIsBlank_ThrowsException() {
        validFilm.setName("   ");
        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_DescriptionTooLong_ThrowsException() {
        validFilm.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ReleaseDateBeforeBirthOfCinema_ThrowsException() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_DurationZero_ThrowsException() {
        validFilm.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void updateFilm_ValidFilm_Success() {
        Film created = filmController.create(validFilm);

        Film updateData = new Film();
        updateData.setId(created.getId());
        updateData.setName("Обновленный фильм");
        updateData.setDescription("Новое описание");
        updateData.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateData.setDuration(150);
        updateData.setMpa(created.getMpa());

        Film updated = filmController.update(updateData);

        assertEquals("Обновленный фильм", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(150, updated.getDuration());
    }

    @Test
    void updateFilm_IdNotExists_ThrowsException() {
        Film updateData = new Film();
        updateData.setId(999L);
        updateData.setName("Фильм");
        updateData.setDescription("Описание");
        updateData.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateData.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        updateData.setMpa(mpa);

        assertThrows(NotFoundException.class, () -> filmController.update(updateData));
    }

    @Test
    void getFilmById_ValidId_Success() {
        Film created = filmController.create(validFilm);
        Film found = filmController.getFilmById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getName(), found.getName());
    }

    @Test
    void getFilmById_InvalidId_ThrowsException() {
        assertThrows(NotFoundException.class, () -> filmController.getFilmById(999L));
    }

    @Test
    void createUser_ValidUser_Success() {
        User created = userController.create(validUser);
        assertNotNull(created.getId());
        assertEquals("test@example.com", created.getEmail());
    }

    @Test
    void createUser_EmailNull_ThrowsException() {
        validUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_EmailWithoutAt_ThrowsException() {
        validUser.setEmail("testexample.com");
        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_NameNull_UsesLogin() {
        validUser.setName(null);
        User created = userController.create(validUser);
        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void createUser_BirthdayInFuture_ThrowsException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void updateUser_ValidUser_Success() {
        User created = userController.create(validUser);

        User updateData = new User();
        updateData.setId(created.getId());
        updateData.setEmail("new@example.com");
        updateData.setLogin("newlogin");
        updateData.setName("Новое Имя");
        updateData.setBirthday(LocalDate.of(1995, 5, 5));

        User updated = userController.update(updateData);

        assertEquals("new@example.com", updated.getEmail());
        assertEquals("newlogin", updated.getLogin());
        assertEquals("Новое Имя", updated.getName());
        assertEquals(LocalDate.of(1995, 5, 5), updated.getBirthday());
    }

    @Test
    void updateUser_IdNotExists_ThrowsException() {
        User updateData = new User();
        updateData.setId(999L);
        updateData.setEmail("test@example.com");
        updateData.setLogin("login");
        updateData.setName("Имя");
        updateData.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(NotFoundException.class, () -> userController.update(updateData));
    }

    @Test
    void getUserById_ValidId_Success() {
        User created = userController.create(validUser);
        User found = userController.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getEmail(), found.getEmail());
    }

    @Test
    void getUserById_InvalidId_ThrowsException() {
        assertThrows(NotFoundException.class, () -> userController.getUserById(999L));
    }

    @Test
    void addFriend_Success() {
        User user1 = userController.create(validUser);

        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendlogin");
        user2.setName("Друг");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        user2 = userController.create(user2);

        userController.addFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }

    @Test
    void removeFriend_Success() {
        User user1 = userController.create(validUser);

        User user2 = new User();
        user2.setEmail("friend2@example.com");
        user2.setLogin("friend2login");
        user2.setName("Друг 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        user2 = userController.create(user2);

        userController.addFriend(user1.getId(), user2.getId());
        userController.removeFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_Success() {
        User user1 = userController.create(validUser);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        user2 = userController.create(user2);

        User common = new User();
        common.setEmail("common@example.com");
        common.setLogin("commonlogin");
        common.setName("Common");
        common.setBirthday(LocalDate.of(1993, 3, 3));
        common = userController.create(common);

        userController.addFriend(user1.getId(), common.getId());
        userController.addFriend(user2.getId(), common.getId());

        List<User> commonFriends = userController.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(common.getId(), commonFriends.get(0).getId());
    }

    @Test
    void addLike_Success() {
        User user = userController.create(validUser);
        Film film = filmController.create(validFilm);

        filmController.addLike(film.getId(), user.getId());

        Film updatedFilm = filmController.getFilmById(film.getId());
        assertEquals(1, updatedFilm.getLikes().size());
        assertTrue(updatedFilm.getLikes().contains(user.getId()));
    }

    @Test
    void removeLike_Success() {
        User user = userController.create(validUser);
        Film film = filmController.create(validFilm);

        filmController.addLike(film.getId(), user.getId());
        filmController.removeLike(film.getId(), user.getId());

        Film updatedFilm = filmController.getFilmById(film.getId());
        assertTrue(updatedFilm.getLikes().isEmpty());
    }

    @Test
    void getPopularFilms_Success() {
        User user1 = userController.create(validUser);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        user2 = userController.create(user2);

        Film film1 = filmController.create(validFilm);

        Film film2 = new Film();
        film2.setName("Второй фильм");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film2.setMpa(mpa);

        film2 = filmController.create(film2);

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user2.getId());
        filmController.addLike(film2.getId(), user1.getId());

        List<Film> popular = filmController.getPopularFilms(10);

        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }
}