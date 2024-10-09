package com.example.ecm.pojo;

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
    private Integer valueId;
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;
    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;
    private String value;
}
