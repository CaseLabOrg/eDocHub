package com.example.ecm.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления данных подписи.
 * Используется для передачи информации о подписи между клиентом и сервером.
 */
@Setter
@Getter
public class CreateSignatureRequest {

    /**
     * Уникальный идентификатор подписи.
     * Может быть null для новых подписей, пока не будет сохранен в базе данных.
     */
    private Long id;

    /**
     * Хэш подписи.
     * Хранит цифровую подпись в зашифрованном виде.
     */
    private Integer hash;

    /**
     * Название окошка для подписи.
     * Используется для идентификации подписи, если в документе много подписей.
     */
    private String placeholderTitle;

    /**
     * Пользователь, которому принадлежит подпись.
     * Определяет, кто оставил подпись.
     */
    private Long userId;
}
