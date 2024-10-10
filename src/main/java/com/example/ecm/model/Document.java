package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс-сущность, представляющий документ в системе.
 * Документ содержит информацию о его атрибутах и связан с пользователем и типом документа.
 */
@Entity
@Table(name="documents")
@Setter
@Getter
public class Document {
    /** Уникальный идентификатор документа */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Заголовок документа, обязательное поле */
    @Column(name = "title", nullable = false)
    private String title;

    /** Пользователь, который создал документ */
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Тип документа, к которому относится данный документ */
    @ManyToOne()
    @JoinColumn(name = "type_id", nullable = false)
    private DocumentType documentType;

    /** Описание документа */
    @Column(name = "description")
    private String description;

    /** Дата и время создания документа, обязательное поле */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    /** Версия документа, обязательное поле */
    @Column(name = "version", nullable = false)
    private Integer version;

    /** Атрибуты документа, хранящиеся в виде карты, связывающей атрибуты с их значениями */
    @OneToMany(mappedBy = "document")
    @MapKeyJoinColumn(name = "attribute_id")
    private Map<Attribute, Value> values = new HashMap<>();
}
