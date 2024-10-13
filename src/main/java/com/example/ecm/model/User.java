package com.example.ecm.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс сущности User, представляющий пользователя в системе.
 * Включает поля: id, FirstName, MiddleName, LastName, email.
 * Используется для хранения информации о пользователях в базе данных.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /**
     * Уникальный идентификатор пользователя. Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Электронная почта пользователя(логин). Должна быть уникальной и не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Имя пользователя. Не может быть null.
     */
    @Column(name="name", nullable = false)
    private String FirstName;

    /**
     * Отчество пользователя. Может быть null.
     */
    @Column
    private String MiddleName;

    /**
     * Фамилия пользователя. Не может быть null.
     */
    @Column(name="surname", nullable = false)
    private String LastName;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}
