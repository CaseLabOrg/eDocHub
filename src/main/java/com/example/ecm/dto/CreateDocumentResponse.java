package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import com.example.ecm.model.Value;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO (Data Transfer Object) для ответа при создании или запросе документа.
 * Этот класс используется для передачи данных документа в ответе на запросы REST API.
 */
@Getter
@Setter
public class CreateDocumentResponse {

    /**
     * Уникальный идентификатор документа.
     */
    private Long id;

    /**
     * Пользователь, который создает или загружает документ.
     */
    private CreateUserResponse user;

    /**
     * Тип документа, определяющий его классификацию.
     */
    private CreateDocumentTypeResponse documentType;

    private List<CreateDocumentVersionResponse> documentVersions;
}
