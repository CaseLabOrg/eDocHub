package com.example.ecm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Класс сущности Role, представляющий роль в системе.
 */
@Getter
@Setter
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
     * Множество пользователей роли.
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
