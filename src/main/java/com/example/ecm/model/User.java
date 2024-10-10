package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс сущности User, представляющий пользователя в системе.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя. Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя. Не может быть null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Фамилия пользователя. Не может быть null.
     */
    @Column(nullable = false)
    private String surname;

    /**
     * Электронная почта пользователя. Должна быть уникальной и не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Пароль пользователя, не может быть null.
     */
    @Column(nullable = false)
    private String password;
}
