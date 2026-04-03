package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;

public interface MpaStorage {
    Mpa getMpaById(Integer id);
    List<Mpa> getAllMpa();
}
