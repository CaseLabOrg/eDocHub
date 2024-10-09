package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Сущность, представляющая тип документа.
 * Используется для классификации документов по их типам.
 */
@Data
@Entity
@Table(name = "document_types")
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
}
