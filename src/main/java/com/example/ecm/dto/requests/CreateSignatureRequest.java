package com.example.ecm.dto.requests;

import com.example.ecm.model.enums.SignatureRequestState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private SignatureRequestState status;
}
