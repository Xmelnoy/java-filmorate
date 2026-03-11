package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Логин не должен быть пустым")
        /*
        * Не совсем понял или плохо искал.
        * в дополнительном задании по ТЗ написано, что логин не должен содержать пробелов,
        * я не понял через какую анотацию это делается, если сможете, оставьте коментарий насчет этого
        * */
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;
    private String name;

    @NotNull(message = "Дата рождения не должна быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}
