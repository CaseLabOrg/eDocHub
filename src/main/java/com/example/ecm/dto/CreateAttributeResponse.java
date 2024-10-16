package com.example.ecm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) для ответа на запрос создания или обновления атрибута документа.
 * Этот класс используется для передачи данных атрибута документа обратно в ответе от REST API
 * после его создания или обновления.
 */
@Getter
@Setter
public class CreateAttributeResponse {

    /**
     * Уникальный идентификатор атрибута
     */
    private Long id;
    /**
     * Название типа документа, к которому относится атрибут
     */
    private String documentTypeName;

    /**
     * Имя атрибута, которое описывает его назначение
     */
    private String name;

    /**
     * Флаг, указывающий, является ли данный атрибут обязательным для заполнения
     */
    private Boolean required;
}
