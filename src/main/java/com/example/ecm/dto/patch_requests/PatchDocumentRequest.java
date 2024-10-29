package com.example.ecm.dto.patch_requests;

import com.example.ecm.dto.requests.SetValueRequest;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Getter
@Setter
public class PatchDocumentRequest {
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
