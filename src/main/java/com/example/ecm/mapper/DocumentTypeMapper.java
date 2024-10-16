package com.example.ecm.mapper;

import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.model.DocumentType;
import org.springframework.stereotype.Component;

/**
 * Компонент для маппинга данных между DTO (Data Transfer Objects) и сущностью DocumentType.
 * Используется для преобразования данных запросов и ответов в объекты модели DocumentType и обратно.
 */
@Component
public class DocumentTypeMapper {

    /**
     * Преобразует запрос на создание типа документа (CreateDocumentTypeRequest) в объект сущности DocumentType.
     *
     * @param request - запрос на создание типа документа.
     * @return объект модели DocumentType, содержащий данные из запроса.
     */
    public DocumentType toDocumentType(CreateDocumentTypeRequest request) {
        DocumentType documentType = new DocumentType();
        documentType.setName(request.getName());
        return documentType;
    }

    /**
     * Преобразует сущность DocumentType в ответ на запрос создания типа документа (CreateDocumentTypeResponse).
     *
     * @param documentType - сущность типа документа.
     * @return объект CreateDocumentTypeResponse, содержащий данные типа документа.
     */
    public CreateDocumentTypeResponse toCreateDocumentTypeResponse(DocumentType documentType) {
        CreateDocumentTypeResponse createDocumentTypeResponse = new CreateDocumentTypeResponse();
        createDocumentTypeResponse.setId(documentType.getId());
        createDocumentTypeResponse.setName(documentType.getName());
        createDocumentTypeResponse.setAttributes(documentType.getAttributes());
        return createDocumentTypeResponse;
    }
}
