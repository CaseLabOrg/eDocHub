package com.example.ecm.model;

import com.example.ecm.model.enums.DocumentState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    /** Пользователь, который создал документ */

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Тип документа, к которому относится данный документ */
    @ManyToOne()
    @JoinColumn(name = "type_id", nullable = false)
    private DocumentType documentType;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<DocumentVersion> documentVersions = new ArrayList<>();

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DocumentState state = DocumentState.CREATED;

    private Boolean isAlive = true;
}
