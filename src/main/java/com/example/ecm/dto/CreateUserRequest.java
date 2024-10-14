package com.example.ecm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

/**
 * Класс DTO для запроса на создание пользователя.
 * Используется для передачи данных от клиента при создании нового пользователя.
 */
@Getter
public class CreateUserRequest {

    /**
     * Имя пользователя. Не может быть null.
     */
    @NotNull
    private String name;

    /**
     * Фамилия пользователя. Не может быть null.
     */
    @NotNull
    private String surname;

    /**
     * Email пользователя. Должен соответствовать формату email и не может быть null.
     */
    @NotNull
    @Email
    private String email;

    /**
     * Пароль пользователя. Не может быть null.
     */
    @NotNull
    private String password;

    @NotNull
    private Set<RoleRequest> roles;
}