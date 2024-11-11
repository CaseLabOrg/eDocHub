package com.example.ecm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс-сущность, представляющий версию документа.
 * Описывает свойства версий и связан с определённым документом.
 */
@Entity
@Table(name="document_version")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** Уникальный идентификатор, нужен для того, чтобы не использовать составной первичный ключ */
    private Long id;
    private Long versionId;

    /** Документ, обязательное поле */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "document_id")
    private Document document;

    /** Заголовок документа, обязательное поле */
    @Column(name = "title", nullable = false)
    private String title;

    /** Описание документа */
    @Column
    private String description;

    /** Дата создания документа, обязательное поле */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Атрибуты документа, хранящиеся в виде карты, связывающей атрибуты с их значениями
     */
    @OneToMany(mappedBy = "documentVersion", cascade = {CascadeType.REMOVE})
    @MapKeyJoinColumn(name = "attribute_id")
    private Map<Attribute, Value> values = new HashMap<>();


    /** Подписи документа */
    @OneToMany(mappedBy = "documentVersion", fetch = FetchType.LAZY)
    private List<Signature> signatures = new ArrayList<>();

    private Boolean isAlive;
}
