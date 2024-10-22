package com.example.ecm.dto.requests;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreateDocumentVersionRequest {

    /**
     * Заголовок документа.
     */
    private String title;


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
