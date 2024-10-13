package com.example.ecm.mapper;

import com.example.ecm.dto.AttributeRequest;
import com.example.ecm.dto.AttributeResponse;
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
    public Attribute toAttribute(AttributeRequest request, DocumentType documentType) {
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
    public AttributeResponse toAttributeResponse(Attribute attribute) {
        AttributeResponse attributeResponse = new AttributeResponse();
        attributeResponse.setId(attribute.getId());
        attributeResponse.setDocumentTypeName(attribute.getDocumentType().getName());
        attributeResponse.setName(attribute.getName());
        attributeResponse.setRequired(attribute.getRequired());
        return attributeResponse;
    }
}
