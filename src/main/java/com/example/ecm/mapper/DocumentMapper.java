package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
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
    private final DocumentVersionMapper documentVersionMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
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
    }

    public DocumentVersion toDocumentVersion(CreateDocumentRequest request) {
        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setCreatedAt(LocalDateTime.now());
        documentVersion.setDescription(request.getDescription());
        documentVersion.setTitle(request.getTitle());
        documentVersion.setIsAlive(true);
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
        createDocumentResponse.setDocumentVersions(document.getDocumentVersions().stream().map(documentVersionMapper::toCreateDocumentVersionResponse).toList());
        createDocumentResponse.setUser(userMapper.toCreateUserResponse(document.getUser()));
        createDocumentResponse.setDocumentType(documentTypeMapper.toCreateDocumentTypeResponse(document.getDocumentType()));
        createDocumentResponse.setComments(document.getComments().stream()
                .map(commentMapper::toAddCommentResponse)
                .toList());
        createDocumentResponse.setState(document.getState());

        return createDocumentResponse;
    }
}
