package com.example.ecm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="values")
@Getter
@Setter
public class Value {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    @Column(nullable = false)
    private String value;
}
