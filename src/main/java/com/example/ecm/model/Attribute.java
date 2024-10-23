package com.example.ecm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-сущность, представляющий атрибут документа.
 * Атрибут описывает свойства документа и связан с определённым типом документа.
 */
@Entity
@Table(name="attributes")
@Getter
@Setter
public class Attribute {

    /** Уникальный идентификатор атрибута */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Тип документа, к которому относится атрибут */
    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "document_types_attributes",
            joinColumns = @JoinColumn(name = "id_attribute"),
            inverseJoinColumns = @JoinColumn(name = "id_document_type")
    )
    private List<DocumentType> documentTypes = new ArrayList<>();

    /** Имя атрибута, которое описывает его назначение */
    @Column(nullable = false)
    private String name;

    /** Флаг, указывающий, является ли данный атрибут обязательным для заполнения */
    private Boolean required;

}
