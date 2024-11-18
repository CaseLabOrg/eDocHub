package com.example.ecm.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Класс сущности Tenant, представляющий арендатора (компанию) в системе.
 */
@Entity
@Getter
@Setter
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(mappedBy = "tenant", cascade = CascadeType.ALL)
    private Subscription subscription;

    private Boolean isAlive;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

}
