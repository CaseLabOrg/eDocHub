package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddCommentResponse {
    private Long id;
    private CreateUserResponse author;
    private String content;
    private LocalDateTime createdAt;
}
