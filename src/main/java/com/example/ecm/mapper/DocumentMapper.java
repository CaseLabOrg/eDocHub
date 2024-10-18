package com.example.ecm.mapper;

import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Компонент для маппинга данных между DTO (Data Transfer Objects) и сущностью Document.
 * Используется для преобразования данных запросов и ответов в объекты модели и обратно.
 */
@Component
@RequiredArgsConstructor
public class DocumentMapper {

    private final DocumentTypeMapper documentTypeMapper;
    private final UserMapper userMapper;
    /**
     * Преобразует запрос на создание документа (CreateDocumentRequest) в объект сущности Document.
     *
     * @param request - запрос на создание документа.
     * @return объект модели Document, содержащий данные из запроса.
     */

    public DocumentVersion createDocumentVersion(CreateDocumentRequest request) {
       DocumentVersion documentVersion = new DocumentVersion();
       documentVersion.setCreatedAt(LocalDateTime.now());
       documentVersion.setDescription(request.getDescription());
       documentVersion.setTitle(request.getTitle());
       return documentVersion;

    public Document toDocument(CreateDocumentRequest request) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCreated_at(LocalDateTime.now());
        document.setVersion(request.getVersion());
        return document;
    }

    public DocumentVersion toDocumentVersion(CreateDocumentRequest request) {
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setCreatedAt(LocalDateTime.now());
        documentVersion.setDescription(request.getDescription());
        documentVersion.setTitle(request.getTitle());
        return documentVersion;
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
        createDocumentResponse.setUser(userMapper.toCreateUserResponse(document.getUser()));
        createDocumentResponse.setDocumentType(documentTypeMapper.toCreateDocumentTypeResponse(document.getDocumentType()));

        return createDocumentResponse;
    }
}
