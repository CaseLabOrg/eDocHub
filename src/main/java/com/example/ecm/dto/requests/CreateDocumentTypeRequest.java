package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO (Data Transfer Object) для запроса на создание типа документа.
 * Этот класс используется для передачи данных типа документа при его создании через REST API.
 */
@Getter
@Setter
public class CreateDocumentTypeRequest {

    /**
     * Название типа документа.
     */
    @NotBlank(message = "Name cannot be blank")
    @NotNull(message = "Name cannot be null")
    private String name;


    @NotNull(message = "AttributeIds cannot be null, but can be blank")
    private List<Long> attributeIds;
}
