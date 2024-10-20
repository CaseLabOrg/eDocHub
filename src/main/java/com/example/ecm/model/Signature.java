package com.example.ecm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность подписи
 */
@Getter
@Setter
@Entity
@Table(name = "signatures")
public class Signature {

    /** Уникальный идентификатор документа */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Подпись, хранящаяся в виде хэша */
    @Column(name = "hash", nullable = false)
    private String hash;

    /**
     * Название окошка для подписи;
     * может быть использовано, если в документе много подписей
     * */
    @Column(name = "placeholder_title")
    private String placeholderTitle;

    /** Пользователь, которому принадлежит подпись */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "document_version_id", nullable = false)
    private DocumentVersion documentVersion;
}
