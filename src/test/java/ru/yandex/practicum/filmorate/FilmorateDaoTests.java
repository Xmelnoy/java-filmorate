package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmorateDaoTests {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void shouldAddAndFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Тест Тестов");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.addUser(user);
        User foundUser = userStorage.getUserById(createdUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getLogin()).isEqualTo("testlogin");
        assertThat(foundUser.getName()).isEqualTo("Тест Тестов");
        assertThat(foundUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldlogin");
        user.setName("Старое имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.addUser(user);

        createdUser.setEmail("new@example.com");
        createdUser.setLogin("newlogin");
        createdUser.setName("Новое имя");
        createdUser.setBirthday(LocalDate.of(1995, 5, 5));

        User updatedUser = userStorage.updateUser(createdUser);

        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedUser.getLogin()).isEqualTo("newlogin");
        assertThat(updatedUser.getName()).isEqualTo("Новое имя");
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    void shouldReturnAllUsers() {
        User user = new User();
        user.setEmail("all@example.com");
        user.setLogin("alllogin");
        user.setName("All User");
        user.setBirthday(LocalDate.of(1991, 2, 2));

        userStorage.addUser(user);

        List<User> users = userStorage.getAllUsers();

        assertThat(users).isNotEmpty();
    }

    @Test
    void shouldDeleteUser() {
        User user = new User();
        user.setEmail("delete@example.com");
        user.setLogin("deletelogin");
        user.setName("Delete User");
        user.setBirthday(LocalDate.of(1992, 3, 3));

        User createdUser = userStorage.addUser(user);
        userStorage.deleteUser(createdUser.getId());

        List<User> users = userStorage.getAllUsers();
        assertThat(users.stream().map(User::getId)).doesNotContain(createdUser.getId());
    }

    @Test
    void shouldAddFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);

        userStorage.addFriend(createdUser1.getId(), createdUser2.getId());

        User updatedUser = userStorage.getUserById(createdUser1.getId());

        assertThat(updatedUser.getFriends()).contains(createdUser2.getId());
    }

    @Test
    void shouldRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user11@example.com");
        user1.setLogin("user11login");
        user1.setName("User Eleven");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user22@example.com");
        user2.setLogin("user22login");
        user2.setName("User Twenty Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));

        User createdUser1 = userStorage.addUser(user1);
        User createdUser2 = userStorage.addUser(user2);

        userStorage.addFriend(createdUser1.getId(), createdUser2.getId());
        userStorage.removeFriend(createdUser1.getId(), createdUser2.getId());

        User updatedUser = userStorage.getUserById(createdUser1.getId());

        assertThat(updatedUser.getFriends()).doesNotContain(createdUser2.getId());
    }

    @Test
    void shouldAddAndFindFilmById() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.addFilm(film);
        Film foundFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(foundFilm.getName()).isEqualTo("Тестовый фильм");
        assertThat(foundFilm.getDescription()).isEqualTo("Описание");
        assertThat(foundFilm.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(foundFilm.getDuration()).isEqualTo(120);
        assertThat(foundFilm.getMpa()).isNotNull();
        assertThat(foundFilm.getMpa().getId()).isEqualTo(1);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Старый фильм");
        film.setDescription("Старое описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.addFilm(film);

        createdFilm.setName("Новый фильм");
        createdFilm.setDescription("Новое описание");
        createdFilm.setDuration(150);

        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Новый фильм");
        assertThat(updatedFilm.getDescription()).isEqualTo("Новое описание");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
    }

    @Test
    void shouldReturnAllFilms() {
        Film film = new Film();
        film.setName("Film One");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(110);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmStorage.addFilm(film);

        List<Film> films = filmStorage.getAllFilms();

        assertThat(films).isNotEmpty();
    }

    @Test
    void shouldDeleteFilm() {
        Film film = new Film();
        film.setName("Delete Film");
        film.setDescription("Delete Description");
        film.setReleaseDate(LocalDate.of(2002, 2, 2));
        film.setDuration(90);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.addFilm(film);

        filmStorage.deleteFilm(createdFilm.getId());

        List<Film> films = filmStorage.getAllFilms();
        assertThat(films.stream().map(Film::getId)).doesNotContain(createdFilm.getId());
    }

    @Test
    void shouldAddLike() {
        User user = new User();
        user.setEmail("like@example.com");
        user.setLogin("likelogin");
        user.setName("Like User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.addUser(user);

        Film film = new Film();
        film.setName("Like Film");
        film.setDescription("Like Description");
        film.setReleaseDate(LocalDate.of(2003, 3, 3));
        film.setDuration(130);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.addFilm(film);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        Film updatedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(updatedFilm.getLikes()).contains(createdUser.getId());
    }

    @Test
    void shouldRemoveLike() {
        User user = new User();
        user.setEmail("removelike@example.com");
        user.setLogin("removelikelogin");
        user.setName("Remove Like User");
        user.setBirthday(LocalDate.of(1991, 1, 1));
        User createdUser = userStorage.addUser(user);

        Film film = new Film();
        film.setName("Remove Like Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2004, 4, 4));
        film.setDuration(140);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmStorage.addFilm(film);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        filmStorage.removeLike(createdFilm.getId(), createdUser.getId());

        Film updatedFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(updatedFilm.getLikes()).doesNotContain(createdUser.getId());
    }

    @Test
    void shouldReturnAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertThat(genres).isNotEmpty();
    }

    @Test
    void shouldReturnGenreById() {
        Genre genre = genreStorage.getGenreById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
    }

    @Test
    void shouldReturnAllMpa() {
        List<Mpa> ratings = mpaStorage.getAllMpa();

        assertThat(ratings).hasSize(5);
    }

    @Test
    void shouldReturnMpaById() {
        Mpa mpa = mpaStorage.getMpaById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
    }

    @Test
    void shouldSaveFilmGenres() {
        Film film = new Film();
        film.setName("Genre Film");
        film.setDescription("Genre Description");
        film.setReleaseDate(LocalDate.of(2005, 5, 5));
        film.setDuration(100);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);

        film.setGenres(new LinkedHashSet<>(List.of(genre)));

        Film createdFilm = filmStorage.addFilm(film);
        Film foundFilm = filmStorage.getFilmById(createdFilm.getId());

        assertThat(foundFilm.getGenres()).isNotEmpty();
        assertThat(foundFilm.getGenres().stream().map(Genre::getId)).contains(1);
    }
}