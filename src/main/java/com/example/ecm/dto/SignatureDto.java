package com.example.ecm.dto;

import com.example.ecm.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления данных подписи.
 * Используется для передачи информации о подписи между клиентом и сервером.
 */
@Setter
@Getter
public class SignatureDto {

    /**
     * Уникальный идентификатор подписи.
     * Может быть null для новых подписей, пока не будет сохранен в базе данных.
     */
    private Long id;

    /**
     * Хэш подписи.
     * Хранит цифровую подпись в зашифрованном виде.
     */
    private String hash;

    /**
     * Название окошка для подписи.
     * Используется для идентификации подписи, если в документе много подписей.
     */
    private String placeholderTitle;

    /**
     * Пользователь, которому принадлежит подпись.
     * Определяет, кто оставил подпись.
     */
    private User user;
}
