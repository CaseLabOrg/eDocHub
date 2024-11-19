package com.example.ecm.dto.responses;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TenantResponse {

    private String name;
    private LocalDateTime createdAt;
    private boolean isAlive;
}
