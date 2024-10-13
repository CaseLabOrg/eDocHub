package com.example.ecm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) для создания или обновления атрибута документа.
 * Этот класс используется для передачи данных при создании или обновлении атрибута документа через REST API.
 */
@Getter
@Setter
public class AttributeRequest {

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
