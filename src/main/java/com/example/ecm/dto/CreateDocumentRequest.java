package com.example.ecm.dto;

import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import lombok.Getter;
import lombok.Setter;

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

        /**
         * Версия документа.
         * Поле для указания версии, может использоваться для контроля изменений.
         */
        private Integer version;
        /**
         * Содержание документа
         */
        private String base64Content;

}
