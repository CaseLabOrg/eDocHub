package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
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
    private String name;
}
