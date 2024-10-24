package com.example.ecm.dto.patch_requests;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserRequest {

    /**
     * Имя пользователя. Не может быть null.
     */
    private String name;

    /**
     * Фамилия пользователя.
     */
    private String surname;

    /**
     * Email пользователя. Должен соответствовать формату email и не может быть null.
     */
    @Email(message = "Email is not valid")
    private String email;

    /**
     * Пароль пользователя. Не может быть null.
     */
    private String password;
}