package com.example.ecm.dto.responses;

import lombok.Data;

@Data
public class ActiveUser {
    private Long userId;
    private String userName;
    private Long documentsCreated;
}
