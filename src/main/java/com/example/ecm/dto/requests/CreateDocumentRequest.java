package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO (Data Transfer Object) для создания нового документа.
 * Этот класс используется для передачи данных при создании документа через REST API.
 */
@Getter
@Setter
public class CreateDocumentRequest {

    /**
     * Заголовок документа.
     */
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title cannot be null")
    private String title;

    /**
     * Пользователь, который создает или загружает документ.
     */
    @NotNull(message = "userId cannot be null")
    private Long userId;

    /**
     * Тип документа, определяющий его классификацию.
     */
    @NotNull(message = "documentTypeId cannot be null")
    private Long documentTypeId;

    /**
     * Описание документа, предоставленное пользователем.
     */
    private String description;


    @NotNull(message = "Values cannot be null, but can be blank")
    private List<SetValueRequest> values;

    /**
     * Содержание документа
     */
    private String base64Content;

}
