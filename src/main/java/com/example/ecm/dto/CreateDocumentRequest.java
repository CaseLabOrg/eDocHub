package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import com.example.ecm.model.Value;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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
        private String title;

        /**
         * Пользователь, который создает или загружает документ.
         */
        private Long userId;

        /**
         * Тип документа, определяющий его классификацию.
         */
        private Long documentTypeId;

        /**
         * Описание документа, предоставленное пользователем.
         */
        private String description;


        private List<SetValueRequest> values;

        /**
         * Содержание документа
         */
        private String base64Content;

}
