package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentRequest {
    @NotBlank(message = "Content cannot be blank")
    @NotNull(message = "Content cannot be null")
    private String content;
}
