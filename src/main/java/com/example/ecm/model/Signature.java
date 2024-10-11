package com.example.ecm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность подписи
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signatures")
public class Signature {

    /** Подпись, хранящаяся в виде хэша */
    @Id
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
}
