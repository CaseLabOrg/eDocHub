package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO (Data Transfer Object) для ответа на запрос создания типа документа.
 * Этот класс используется для передачи данных типа документа обратно в ответе от REST API после его создания.
 */
@Getter
@Setter
public class CreateDocumentTypeResponse {

    /**
     * Уникальный идентификатор типа документа.
     */
    private Long id;

    /**
     * Название типа документа.
     */
    private String name;

    /**
     * Список атрибутов, связанных с данным типом документа.
     * Атрибуты определяют характеристики и поля для документов этого типа.
     */
    private List<CreateAttributeResponse> attributes;
}
