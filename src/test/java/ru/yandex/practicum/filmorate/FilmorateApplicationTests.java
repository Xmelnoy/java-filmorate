package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
class FilmorateApplicationTests {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    private Film validFilm;
    private User validUser;

    @BeforeEach
    void setUp() {
        validFilm = createTestFilm("test-film-");
        validUser = createTestUser("test-user-");
    }

    @Test
    void createFilm_ValidFilm_Success() {
        Film created = filmController.create(validFilm);

        assertNotNull(created.getId());
        assertEquals(validFilm.getName(), created.getName());
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
    void createFilm_DescriptionExactly200_Success() {
        String desc200 = "a".repeat(200);
        validFilm.setDescription(desc200);

        Film created = filmController.create(validFilm);

        assertEquals(desc200, created.getDescription());
    }

    @Test
    void createFilm_ReleaseDateBeforeBirthOfCinema_ThrowsException() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ReleaseDateExactlyBirthOfCinema_Success() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film created = filmController.create(validFilm);

        assertNotNull(created.getId());
        assertEquals(validFilm.getName(), created.getName());
    }

    @Test
    void createFilm_DurationZero_ThrowsException() {
        validFilm.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_DurationNegative_ThrowsException() {
        validFilm.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_DurationPositive_Success() {
        validFilm.setDuration(1);

        Film created = filmController.create(validFilm);

        assertEquals(1, created.getDuration());
    }

    @Test
    void updateFilm_ValidFilm_Success() {
        Film created = filmController.create(validFilm);

        Film updateData = createTestFilm("updated-film-");
        updateData.setId(created.getId());
        updateData.setDescription("Новое описание");
        updateData.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateData.setDuration(150);
        updateData.setMpa(created.getMpa());

        Film updated = filmController.update(updateData);

        assertEquals(updateData.getName(), updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(150, updated.getDuration());
    }

    @Test
    void updateFilm_IdNotExists_ThrowsException() {
        Film updateData = createTestFilm("not-found-film-");
        updateData.setId(999L);

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
        assertEquals(validUser.getEmail(), created.getEmail());
    }

    @Test
    void createUser_EmailNull_ThrowsException() {
        validUser.setEmail(null);

        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_EmailBlank_ThrowsException() {
        validUser.setEmail("   ");

        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_EmailWithoutAt_ThrowsException() {
        validUser.setEmail("testexample.com");

        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_EmailWithAt_Success() {
        User created = userController.create(validUser);

        assertEquals(validUser.getEmail(), created.getEmail());
    }

    @Test
    void createUser_NameNull_UsesLogin() {
        validUser.setName(null);

        User created = userController.create(validUser);

        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void createUser_NameBlank_UsesLogin() {
        validUser.setName("   ");

        User created = userController.create(validUser);

        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void createUser_BirthdayInFuture_ThrowsException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(validUser));
    }

    @Test
    void createUser_BirthdayToday_Success() {
        validUser.setBirthday(LocalDate.now());

        User created = userController.create(validUser);

        assertEquals(LocalDate.now(), created.getBirthday());
    }

    @Test
    void createUser_BirthdayPast_Success() {
        validUser.setBirthday(LocalDate.of(1900, 1, 1));

        User created = userController.create(validUser);

        assertEquals(LocalDate.of(1900, 1, 1), created.getBirthday());
    }

    @Test
    void updateUser_ValidUser_Success() {
        User created = userController.create(validUser);

        User updateData = createTestUser("updated-user-");
        updateData.setId(created.getId());
        updateData.setName("Новое Имя");
        updateData.setBirthday(LocalDate.of(1995, 5, 5));

        User updated = userController.update(updateData);

        assertEquals(updateData.getEmail(), updated.getEmail());
        assertEquals(updateData.getLogin(), updated.getLogin());
        assertEquals("Новое Имя", updated.getName());
        assertEquals(LocalDate.of(1995, 5, 5), updated.getBirthday());
    }

    @Test
    void updateUser_IdNotExists_ThrowsException() {
        User updateData = createTestUser("not-found-user-");
        updateData.setId(999L);

        assertThrows(NotFoundException.class, () -> userController.update(updateData));
    }

    @Test
    void createUser_WithoutId_Success() {
        validUser.setId(null);

        User created = userController.create(validUser);

        assertNotNull(created.getId());
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
        User user2 = userController.create(createTestUser("friend-"));

        userController.addFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }

    @Test
    void removeFriend_Success() {
        User user1 = userController.create(validUser);
        User user2 = userController.create(createTestUser("friend-remove-"));

        userController.addFriend(user1.getId(), user2.getId());
        userController.removeFriend(user1.getId(), user2.getId());

        List<User> friends = userController.getFriends(user1.getId());
        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_Success() {
        User user1 = userController.create(validUser);
        User user2 = userController.create(createTestUser("user2-"));
        User common = userController.create(createTestUser("common-"));

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
        User user2 = userController.create(createTestUser("popular-user-"));

        Film film1 = filmController.create(validFilm);
        Film film2 = filmController.create(createTestFilm("second-film-"));

        filmController.addLike(film1.getId(), user1.getId());
        filmController.addLike(film1.getId(), user2.getId());
        filmController.addLike(film2.getId(), user1.getId());

        List<Film> popular = filmController.getPopularFilms(10);

        assertEquals(2, popular.size());
        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }

    private User createTestUser(String prefix) {
        String unique = prefix + System.nanoTime();

        User user = new User();
        user.setEmail(unique + "@example.com");
        user.setLogin(unique);
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private Film createTestFilm(String prefix) {
        String unique = prefix + System.nanoTime();

        Film film = new Film();
        film.setName(unique);
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        return film;
    }
}