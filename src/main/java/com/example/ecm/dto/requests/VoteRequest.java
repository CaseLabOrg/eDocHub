package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequest {

    @NotBlank(message = "decision cannot be blank")
    private String decision;
    @NotNull(message = "documentVersionId cannot be null")
    private Long documentVersionId;
}
