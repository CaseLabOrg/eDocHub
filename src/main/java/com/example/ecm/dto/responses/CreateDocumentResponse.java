package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
