package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
import com.example.ecm.model.Document;
import com.example.ecm.model.Signature;
import com.example.ecm.model.Value;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CreateDocumentVersionResponse {

    private Long id;

    private Long versionId;


    private String title;


    private String description;

    /** Дата создания документа, обязательное поле */

    private LocalDateTime createdAt;

    /**
     * Атрибуты документа, хранящиеся в виде карты, связывающей атрибуты с их значениями
     */

    private List<SetValueRequest> values;

    private String base64Content;

    private List<SignatureDto> signatures;
}
