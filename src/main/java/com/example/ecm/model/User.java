package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс сущности User, представляющий пользователя в системе.
 * Включает поля: id, username, FirstName, MiddleName, LastName, email.
 * Используется для хранения информации о пользователях в базе данных.
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
     * Имя пользователя (логин). Должно быть уникальным и не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Имя пользователя. Не может быть null.
     */
    @Column(nullable = false)
    private String FirstName;

    /**
     * Отчество пользователя. Может быть null.
     */
    @Column
    private String MiddleName;

    /**
     * Фамилия пользователя. Не может быть null.
     */
    @Column(nullable = false)
    private String LastName;

    /**
     * Электронная почта пользователя. Должна быть уникальной и не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String email;
}
