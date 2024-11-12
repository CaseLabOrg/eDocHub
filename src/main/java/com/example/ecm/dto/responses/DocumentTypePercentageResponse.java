package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для представления процентного соотношения документов по типу.
 */
@Setter
@Getter
public class DocumentTypePercentageResponse {

    /** Имя типа документа*/
    private String documentType;

    /** Процентное соотношение документов данного типа от общего количества */
    private Double documentPercentage;

    /**
     * Конструктор класса DocumentTypePercentageDto.
     *
     * @param documentType Имя типа документа
     * @param documentPercentage Процентное соотношение документов данного типа
     */
    public DocumentTypePercentageResponse(String documentType, Double documentPercentage) {
        this.documentType = documentType;
        this.documentPercentage = documentPercentage;
    }
}
