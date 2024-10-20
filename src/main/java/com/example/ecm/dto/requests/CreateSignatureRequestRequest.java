package com.example.ecm.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSignatureRequestRequest {
    private Long userIdTo;
    private Long documentId;
}
