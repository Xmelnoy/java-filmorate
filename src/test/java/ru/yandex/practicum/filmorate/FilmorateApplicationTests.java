package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmorateApplicationTests {
    private FilmController filmController;
    private Film validFilm;

    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Тестовый фильм");
        validFilm.setDescription("Описание");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        );
        validFilm.setDuration(Duration.ofMinutes(120));

        userController = new UserController();
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testlogin");
        validUser.setName("Тест Тестов");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createFilm_ValidFilm_Success() throws ValidationException {
        Film created = filmController.create(validFilm);
        assertNotNull(created.getId());
        assertEquals("Тестовый фильм", created.getName());
    }

    @Test
    void createFilm_NameIsNull_ThrowsException() {
        validFilm.setName(null);
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_NameIsBlank_ThrowsException() {
        validFilm.setName("   ");
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_DescriptionTooLong_ThrowsException() {
        String longDesc = "a".repeat(201);
        validFilm.setDescription(longDesc);
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_DescriptionExactly200_Success() throws ValidationException {
        String desc200 = "a".repeat(200);
        validFilm.setDescription(desc200);
        Film created = filmController.create(validFilm);
        assertEquals(desc200, created.getDescription());
    }

    @Test
    void createFilm_ReleaseDateBeforeBirthOfCinema_ThrowsException() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        );
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_ReleaseDateExactlyBirthOfCinema_Success() throws ValidationException {
        Instant expected = LocalDate.of(1895, 12, 28)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        validFilm.setReleaseDate(expected);

        Film created = filmController.create(validFilm);
        assertEquals(expected, created.getReleaseDate());
    }

    @Test
    void createFilm_DurationZero_ThrowsException() {
        validFilm.setDuration(Duration.ofMinutes(0));
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_DurationNegative_ThrowsException() {
        validFilm.setDuration(Duration.ofMinutes(-10));
        assertThrows(ValidationException.class, () ->
                filmController.create(validFilm)
        );
    }

    @Test
    void createFilm_DurationPositive_Success() throws ValidationException {
        validFilm.setDuration(Duration.ofMinutes(1));
        Film created = filmController.create(validFilm);
        assertEquals(Duration.ofMinutes(1), created.getDuration());
    }

    @Test
    void updateFilm_ValidFilm_Success() throws ValidationException {
        Film created = filmController.create(validFilm);
        Film updateData = new Film();
        updateData.setId(created.getId());
        updateData.setName("Обновленный фильм");
        updateData.setDescription("Новое описание");
        updateData.setReleaseDate(LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        );
        updateData.setDuration(Duration.ofMinutes(150));

        Film updated = filmController.update(updateData);
        assertEquals("Обновленный фильм", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(Duration.ofMinutes(150), updated.getDuration());
    }

    @Test
    void updateFilm_IdNotExists_ThrowsException() {
        Film updateData = new Film();
        updateData.setId(999L);
        updateData.setName("Фильм");
        updateData.setDescription("Описание");
        updateData.setReleaseDate(LocalDate.of(2000, 1, 1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
        );
        updateData.setDuration(Duration.ofMinutes(120));

        assertThrows(ValidationException.class, () ->
                filmController.update(updateData)
        );
    }

    @Test
    void createUser_ValidUser_Success() throws ValidationException {
        User created = userController.create(validUser);
        assertNotNull(created.getId());
        assertEquals("test@example.com", created.getEmail());
    }

    @Test
    void createUser_EmailNull_ThrowsException() {
        validUser.setEmail(null);
        assertThrows(ValidationException.class, () ->
                userController.create(validUser)
        );
    }

    @Test
    void createUser_EmailBlank_ThrowsException() {
        validUser.setEmail("   ");
        assertThrows(ValidationException.class, () ->
                userController.create(validUser)
        );
    }

    @Test
    void createUser_EmailWithoutAt_ThrowsException() {
        validUser.setEmail("testexample.com");
        assertThrows(ValidationException.class, () ->
                userController.create(validUser)
        );
    }

    @Test
    void createUser_EmailWithAt_Success() throws ValidationException {
        validUser.setEmail("test@example.com");
        User created = userController.create(validUser);
        assertEquals("test@example.com", created.getEmail());
    }

    @Test
    void createUser_NameNull_UsesLogin() throws ValidationException {
        validUser.setName(null);
        User created = userController.create(validUser);
        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void createUser_NameBlank_UsesLogin() throws ValidationException {
        validUser.setName("   ");
        User created = userController.create(validUser);
        assertEquals(validUser.getLogin(), created.getName());
    }

    @Test
    void createUser_BirthdayNull_ThrowsException() {
        validUser.setBirthday(null);
        assertThrows(ValidationException.class, () ->
                userController.create(validUser)
        );
    }

    @Test
    void createUser_BirthdayInFuture_ThrowsException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () ->
                userController.create(validUser)
        );
    }

    @Test
    void createUser_BirthdayToday_Success() throws ValidationException {
        validUser.setBirthday(LocalDate.now());
        User created = userController.create(validUser);
        assertEquals(LocalDate.now(), created.getBirthday());
    }

    @Test
    void createUser_BirthdayPast_Success() throws ValidationException {
        validUser.setBirthday(LocalDate.of(1900, 1, 1));
        User created = userController.create(validUser);
        assertEquals(LocalDate.of(1900, 1, 1), created.getBirthday());
    }

    @Test
    void updateUser_ValidUser_Success() throws ValidationException {
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

        assertThrows(ValidationException.class, () ->
                userController.update(updateData)
        );
    }

    @Test
    void createUser_WithoutId_Success() throws ValidationException {
        validUser.setId(null);
        User created = userController.create(validUser);
        assertNotNull(created.getId());
    }
}
