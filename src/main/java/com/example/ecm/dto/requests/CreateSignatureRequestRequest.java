package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSignatureRequestRequest {
    @NotNull(message = "userIdTo cannot be null")
    private Long userIdTo;
    @NotBlank(message = "documentVersionId cannot be blank")
    @NotNull(message = "documentVersionId cannot be null")
    private Long documentVersionId;
    @NotBlank(message = "documentId cannot be blank")
    @NotNull(message = "documentId cannot be null")
    private Long documentId;
}
