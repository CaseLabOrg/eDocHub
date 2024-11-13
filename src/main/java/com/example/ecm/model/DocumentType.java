package com.example.ecm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая тип документа.
 * Используется для классификации документов по их типам.
 */
@Entity
@Table(name = "document_types")
@Getter
@Setter
public class DocumentType {

    /**
     * Уникальный идентификатор типа документа.
     * Генерируется автоматически.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название типа документа.
     * Это поле является обязательным и должно быть уникальным.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Список атрибутов, связанных с типом документа.
     * Используется для хранения характеристик, которые могут быть применены к документам данного типа.
     */
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "document_types_attributes",
            joinColumns = @JoinColumn(name = "id_document_type"),
            inverseJoinColumns = @JoinColumn(name = "id_attribute")
    )
    private List<Attribute> attributes = new ArrayList<>();

    private Boolean isAlive = true;
}
