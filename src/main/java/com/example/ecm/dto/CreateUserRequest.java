package com.example.ecm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс DTO для запроса на создание пользователя.
 * Используется для передачи данных от клиента при создании нового пользователя.
 */
@Getter
@Setter
public class CreateUserRequest {

    /**
     * Имя пользователя. Не может быть null.
     */
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    private String name;

    /**
     * Фамилия пользователя. Не может быть null.
     */
    @NotBlank(message = "Surname cannot be blank")
    @NotNull(message = "Surname cannot be null")
    private String surname;

    /**
     * Email пользователя. Должен соответствовать формату email и не может быть null.
     */
    @Email(message = "Email is not email")
    @NotNull(message = "Email cannot be null")
    private String email;

    /**
     * Пароль пользователя. Не может быть null.
     */
    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    private String password;
}