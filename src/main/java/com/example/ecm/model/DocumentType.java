package com.example.ecm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    /**
     * Название типа документа.
     * Это поле является обязательным и должно быть уникальным.
     */
    @Column(nullable = false, unique = true)
    String name;

    /**
     * Список атрибутов, связанных с типом документа.
     * Используется для хранения характеристик, которые могут быть применены к документам данного типа.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "documentType", fetch = FetchType.EAGER)
    private List<Attribute> attributes;
}
