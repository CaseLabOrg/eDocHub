package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для представления количества документов по типу.
 */
@Setter
@Getter
public class DocumentCountByTypeResponse {

    /** Имя типа документа*/
    private String documentType;

    /** Количество документов данного типа */
    private Long documentCount;

    /**
     * Конструктор класса DocumentCountByTypeDto.
     *
     * @param documentType Имя типа документа
     * @param documentCount Количество документов данного типа
     */
    public DocumentCountByTypeResponse(String documentType, Long documentCount) {
        this.documentType = documentType;
        this.documentCount = documentCount;
    }
}
