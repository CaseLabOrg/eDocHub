package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<User> users = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "department_departments",
    joinColumns = @JoinColumn(name = "root_id"),
            inverseJoinColumns = @JoinColumn(name = "children_id")
    )
    private List<Department> children = new ArrayList<>();


    @OneToMany(mappedBy = "department")
    private List<Document> documents = new ArrayList<>();
}
