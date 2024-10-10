package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность, представляющая значение атрибута документа.
 * Используется для хранения конкретных значений, связанных с атрибутами документов.
 */
@Getter
@Setter
@Entity
@Table(name = "values")
public class Value {

    /**
     * Уникальный идентификатор значения.
     * Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Значение атрибута.
     * Это поле является обязательным.
     */
    @Column(name = "value", nullable = false)
    private String value;

    /**
     * Связь с документом, к которому относится это значение.
     * Указывает на документ, которому принадлежит данное значение атрибута.
     */
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    /**
     * Связь с атрибутом, к которому принадлежит это значение.
     * Указывает на атрибут, для которого это значение является конкретным.
     */
    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;
}
