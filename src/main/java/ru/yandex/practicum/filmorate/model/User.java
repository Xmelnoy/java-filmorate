package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
    private String login;
    private String name;

    @NotNull(message = "Дата рождения не должна быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
