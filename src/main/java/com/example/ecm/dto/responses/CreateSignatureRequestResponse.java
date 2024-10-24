package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSignatureRequestResponse {
    private Long id;
    private CreateUserResponse userTo;
    private Long documentVersionId;
}