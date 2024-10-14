package com.example.ecm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Класс DTO для ответа после создания пользователя.
 * Используется для передачи данных от сервера клиенту после успешного создания пользователя.
 */
@Setter
@Getter
public class CreateUserResponse {

    /**
     * Идентификатор созданного пользователя.
     */
    private Long id;

    /**
     * Имя созданного пользователя.
     */
    private String name;

    /**
     * Фамилия созданного пользователя.
     */
    private String surname;

    /**
     * Email созданного пользователя.
     */
    private String email;

    /**
     * Множество ролей созданного пользователя.
     */
    private Set<RoleResponse> roles;
}