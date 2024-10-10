package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="documents")
@Setter
@Getter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private DocumentType documentType;

    private String description;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "document")
    @MapKeyJoinColumn(name = "attribute_id")
    private Map<Attribute, Value> values = new HashMap<>();
}

