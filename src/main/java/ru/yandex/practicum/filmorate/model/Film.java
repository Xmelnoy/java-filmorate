package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не должна быть пустой")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не должна быть пустой")
    private int duration;

    private Set<Long> likes = new HashSet<>();
}
