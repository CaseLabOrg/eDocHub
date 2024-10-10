package com.example.ecm.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name="documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "type_id", nullable = false)
    private DocumentType documentType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "version", nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "document")
    @MapKeyJoinColumn(name = "attribute_id")
    private Map<Attribute, Value> values = new HashMap<>();
}
