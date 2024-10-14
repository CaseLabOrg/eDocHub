package com.example.ecm.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Класс сущности User, представляющий пользователя в системе.
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

    /**
     * Роли пользователя
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
