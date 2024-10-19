package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSignatureRequestRequest {
    @NotNull(message = "userIdTo cannot be null")
    private Long userIdTo;
    @NotNull(message = "documentVersionId cannot be null")
    private Long documentVersionId;
}
