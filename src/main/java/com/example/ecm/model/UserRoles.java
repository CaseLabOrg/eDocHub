package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Класс сущности UserRoles, представляющий промежуточную таблицу между пользователями и ролями.
 */
@Data
@Entity
@Table(name = "user_roles")
public class UserRoles {

    /**
     * Уникальный идентификатор записи в таблице user_roles. Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, которому назначена роль. Внешний ключ на таблицу users.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Роль, назначенная пользователю. Внешний ключ на таблицу roles.
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
