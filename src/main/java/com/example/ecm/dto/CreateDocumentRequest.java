package com.example.ecm.dto;

import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
        private User user;

        /**
         * Тип документа, определяющий его классификацию.
         */
        private DocumentType documentType;

        /**
         * Описание документа, предоставленное пользователем.
         */
        private String description;

        /**
         * Время создания документа.
         * Это поле может быть установлено системой или передано пользователем.
         */
        private LocalDateTime created_at;

        /**
         * Версия документа.
         * Поле для указания версии, может использоваться для контроля изменений.
         */
        private Integer version;

}
