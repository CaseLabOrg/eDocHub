package com.example.ecm.pojo;

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
    private Integer documentId;
    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="type_id")
    private DocumentType documentType;
    private LocalDateTime createdAt;
    private Integer version;

    @OneToMany(mappedBy = "document")
    @MapKeyJoinColumn(name = "attribute_id")
    private Map<Attribute, Value> values = new HashMap<>();
}
