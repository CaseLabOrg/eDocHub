package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import com.example.ecm.model.Value;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
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
     * Версия документа.
     */
    private Long versionId;

    /**
     * Заголовок документа.
     */
    private String title;

    /**
     * Пользователь, который создает или загружает документ.
     */
    private CreateUserResponse user;

    /**
     * Тип документа, определяющий его классификацию.
     */
    private CreateDocumentTypeResponse documentType;

    /**
     * Описание документа.
     */
    private String description;

    /**
     * Дата и время создания документа.
     */
    private LocalDateTime created_at;


    /**
     * Карта атрибутов документа и их значений.
     * Хранит пары ключ-значение для кастомных атрибутов документа.
     */
    private Map<Attribute, Value> values = new HashMap<>();

    /**
     * Метод для установки или добавления атрибутов документа.
     * Если карта значений не пустая, новые значения добавляются к существующим.
     *
     * @param values карта атрибутов и их значений для добавления в документ.
     */
    public void setValues(Map<Attribute, Value> values) {
        if (this.values == null) {
            this.values = new HashMap<>();
        }
        if (values != null) {
            this.values.putAll(values);  // Добавляем все новые значения в существующую карту
        }
    }

    /**
     * Содержание документа
     */

    private String base64Content;

}
