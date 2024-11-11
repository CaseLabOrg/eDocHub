package com.example.ecm.dto.responses;

import lombok.Data;

@Data
public class SignatureStatus {
    private String status;
    private Long requestCount;
}
