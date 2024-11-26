package com.example.ecm.dto.responses;

import com.example.ecm.model.enums.SignatureRequestState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSignatureRequestResponse {
    private Long id;
    private CreateUserResponse userTo;
    private SignatureRequestState status;
    private Long votingId;
    private Long documentId;
    private Long documentVersionId;
}