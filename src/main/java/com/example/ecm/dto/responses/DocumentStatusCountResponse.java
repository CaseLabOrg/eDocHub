package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для представления количества документов по статусу (активный/неактивный).
 */
@Setter
@Getter
public class DocumentStatusCountResponse {

    /** Статус документа*/
    private Boolean isAlive;

    /** Количество документов с данным статусом */
    private Long documentCount;

    /**
     * Конструктор класса DocumentStatusCountDto.
     *
     * @param isAlive Статус документа
     * @param documentCount Количество документов с данным статусом
     */
    public DocumentStatusCountResponse(Boolean isAlive, Long documentCount) {
        this.isAlive = isAlive;
        this.documentCount = documentCount;
    }
}
