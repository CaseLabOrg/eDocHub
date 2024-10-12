package com.example.ecm.mapper;

import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.model.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Компонент для маппинга данных между DTO (Data Transfer Objects) и сущностью Document.
 * Используется для преобразования данных запросов и ответов в объекты модели и обратно.
 */
@Component
public class DocumentMapper {

    /**
     * Преобразует запрос на создание документа (CreateDocumentRequest) в объект сущности Document.
     *
     * @param request - запрос на создание документа.
     * @return объект модели Document, содержащий данные из запроса.
     */
    public Document toDocument(CreateDocumentRequest request) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setUser(request.getUser());
        document.setDocumentType(request.getDocumentType());
        document.setDescription(request.getDescription());
        document.setCreated_at(LocalDateTime.now());
        document.setVersion(request.getVersion());
        return document;
    }

    /**
     * Преобразует сущность Document в ответ на запрос создания документа (CreateDocumentResponse).
     *
     * @param document - сущность документа.
     * @return объект CreateDocumentResponse, содержащий данные документа.
     */
    public CreateDocumentResponse toCreateDocumentResponse(Document document) {
        CreateDocumentResponse createDocumentResponse = new CreateDocumentResponse();
        createDocumentResponse.setId(document.getId());
        createDocumentResponse.setTitle(document.getTitle());
        createDocumentResponse.setUser(document.getUser());
        createDocumentResponse.setDocumentType(document.getDocumentType());
        createDocumentResponse.setDescription(document.getDescription());
        createDocumentResponse.setCreated_at(document.getCreated_at());
        createDocumentResponse.setVersion(document.getVersion());
        createDocumentResponse.setValues(document.getValues());
        return createDocumentResponse;
    }
}
