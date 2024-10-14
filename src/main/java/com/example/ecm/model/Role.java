package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс сущности Role, представляющий роль в системе.
 */
@Data
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Уникальный идентификатор роли. Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название роли. Должно быть уникальным и не может быть null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
