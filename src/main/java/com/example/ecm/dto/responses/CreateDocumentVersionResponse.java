package com.example.ecm.dto.responses;

import com.example.ecm.dto.requests.SetValueRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<GetSignatureResponse> signatures;
}
