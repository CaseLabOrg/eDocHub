package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.requests.SetValueRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.model.*;
import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import com.example.ecm.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Компонент для маппинга данных между DTO (Data Transfer Objects) и сущностью Document.
 * Используется для преобразования данных запросов и ответов в объекты модели и обратно.
 */
@Component
@RequiredArgsConstructor
public class DocumentMapper {

    private final DocumentTypeMapper documentTypeMapper;
    private final UserMapper userMapper;
    private final AttributeService attributeService;
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
        createDocumentResponse.setUser(userMapper.toCreateUserResponse(document.getUser()));
        createDocumentResponse.setDocumentType(documentTypeMapper.toCreateDocumentTypeResponse(document.getDocumentType()));
        createDocumentResponse.setComments(document.getComments().stream()
                .map(commentMapper::toAddCommentResponse)
                .toList());

        return createDocumentResponse;
    }

    public static DocumentElasticsearch toDocumentElasticsearch(CreateDocumentRequest request) {

        DocumentElasticsearch documentElasticsearch = new DocumentElasticsearch();

        documentElasticsearch.setId(UUID.randomUUID().toString());

        documentElasticsearch.setTitle(request.getTitle());
        documentElasticsearch.setUserId(request.getUserId());
        documentElasticsearch.setDocumentTypeId(request.getDocumentTypeId());
        documentElasticsearch.setDescription(request.getDescription());
        documentElasticsearch.setCreatedAt(OffsetDateTime.now().toEpochSecond());
        documentElasticsearch.setValues(mapValues(request.getValues()));

        return documentElasticsearch;
    }

    private static Map<String, String> mapValues(List<SetValueRequest> values) {
        Map<String, String> valuesMap = new HashMap<>();

        for (SetValueRequest value : values) {
            valuesMap.put(value.getAttributeName(), value.getValue());
        }

        return valuesMap;
    }

    public Document toDocument(DocumentElasticsearch documentElasticsearch, User user, DocumentType documentType, Set<Map.Entry<String, String>> entrySet) {

        Document document = new Document();

        //document.setId(documentElasticsearch.getId());
        document.setUser(user);
        document.setDocumentType(documentType);

        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setDocument(document);
        documentVersion.setTitle(documentElasticsearch.getTitle());
        documentVersion.setDescription(documentElasticsearch.getDescription());
        documentVersion.setCreatedAt(Instant.ofEpochMilli(documentElasticsearch.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDateTime());

        Map<Attribute, Value> values = new HashMap<>();
        for (Map.Entry<String, String> entry : entrySet) {
            Attribute attribute = attributeService.findAttributeByName(entry.getKey()).get();


            Value value = new Value();
            value.setAttribute(attribute);
            value.setValue(entry.getValue());
            value.setDocumentVersion(documentVersion);
            values.put(attribute, value);
        }

        documentVersion.setValues(values);
        document.setDocumentVersions(List.of(documentVersion));

        return document;
    }


    public Map<String, String> convertToMapStringString(Map<Attribute, Value> values) {
        return values.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> entry.getValue().getValue()
                ));
    }

    public Map<Attribute, Value> convertToAttributeValueMap(Map<String, String> values) {
        Map<Attribute, Value> attributeValueMap = new HashMap<>();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            Optional<Attribute> attributeOpt = attributeService.findAttributeByName(entry.getKey());
            attributeOpt.ifPresent(attribute -> {
                Value value = new Value();
                value.setAttribute(attribute);
                value.setValue(entry.getValue());
                attributeValueMap.put(attribute, value);
            });
        }
        return attributeValueMap;
    }
}
