package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    /** Имя атрибута, которое описывает его назначение */
    @Column(nullable = false)
    private String name;

    /** Флаг, указывающий, является ли данный атрибут обязательным для заполнения */
    @Column(nullable = false)
    private String name;

    private Boolean required;
}
