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

@Getter
@Setter
public class CreateDocumentResponse {
    private Long id;

    private String title;

    private User user;

    private DocumentType documentType;

    private String description;

    private LocalDateTime created_at;

    private Integer version;

    private Map<Attribute, Value> values = new HashMap<>();

    public void setValues(Map<Attribute, Value> values) {
        if (this.values == null) {
            this.values = new HashMap<>();
        }
        if (values != null) {
            this.values.putAll(values);  // Добавляем все новые значения в существующую карту
        }
    }

}
