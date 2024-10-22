package com.example.ecm.dto.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления данных подписи.
 * Используется для передачи информации о подписи между клиентом и сервером.
 */
@Setter
@Getter
public class CreateSignatureRequest {

    /**
     * Название окошка для подписи.
     * Используется для идентификации подписи, если в документе много подписей.
     */
    private String placeholderTitle;
    private String status;

    private Long signatureRequestId;
}
