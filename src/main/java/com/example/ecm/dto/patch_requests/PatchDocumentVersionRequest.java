package com.example.ecm.dto.patch_requests;

import com.example.ecm.dto.requests.SetValueRequest;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class PatchDocumentVersionRequest {

    /**
     * Заголовок документа.
     */
    private String title;

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
