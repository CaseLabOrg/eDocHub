package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;
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

    /**
     * Связь с таблицей UserRoles, представляющая пользователей, которым назначена данная роль.
     */
    @OneToMany(mappedBy = "role")
    private Set<UserRoles> userRoles;
}
