package com.example.ecm.mapper;

import com.example.ecm.dto.CreateAttributeRequest;
import com.example.ecm.dto.CreateAttributeResponse;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import org.springframework.stereotype.Component;

/**
 * Компонент для маппинга данных между DTO (Data Transfer Objects) и сущностью Attribute.
 * Используется для преобразования данных запросов и ответов в объекты модели Attribute и обратно.
 */
@Component
public class AttributeMapper {


    /**
     * Преобразует данные для создания или обновлени атрибута документа (AttributeRequest) в объект сущности Attribute.
     *
     * @param request      - запрос на создание атрибута документа.
     * @param documentType - тип документа, необходимый для создания атрибута.
     * @return объект модели Attribute, содержащий данные из запроса.
     */
    public Attribute toAttribute(CreateAttributeRequest request, DocumentType documentType) {
        Attribute attribute = new Attribute();
        attribute.setDocumentType(documentType);
        attribute.setName(request.getName());
        attribute.setRequired(request.getRequired());
        return attribute;
    }

    /**
     * Преобразует сущность Attribute в ответ на запрос создания или обновления атрибута документа (AttributeResponse).
     *
     * @param attribute - сущность атрибута документа.
     * @return объект AttributeResponse, содержащий данные атрибута документа.
     */
    public CreateAttributeResponse toAttributeResponse(Attribute attribute) {
        CreateAttributeResponse createAttributeResponse = new CreateAttributeResponse();
        createAttributeResponse.setId(attribute.getId());
        createAttributeResponse.setDocumentTypeName(attribute.getDocumentType().getName());
        createAttributeResponse.setName(attribute.getName());
        createAttributeResponse.setRequired(attribute.getRequired());
        return createAttributeResponse;
    }
}
