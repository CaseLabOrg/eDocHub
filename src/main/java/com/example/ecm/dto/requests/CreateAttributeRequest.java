package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO (Data Transfer Object) для создания или обновления атрибута документа.
 * Этот класс используется для передачи данных при создании или обновлении атрибута документа через REST API.
 */
@Getter
@Setter
public class CreateAttributeRequest {

    /**
     * Название типа документа, к которому относится атрибут
     */
    @NotNull(message = "DocumentTypesNames cannot be null, but can be blank")
    private List<String> documentTypesNames;

    /**
     * Имя атрибута, которое описывает его назначение
     */
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    private String name;

    /**
     * Флаг, указывающий, является ли данный атрибут обязательным для заполнения
     */
    @NotNull(message = "Required cannot be null")
    private Boolean required;
}
