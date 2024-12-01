package com.example.ecm.dto.responses;


import com.example.ecm.model.Subscription;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TenantResponse {

    private String name;
    private LocalDateTime createdAt;
    private CreateSubscriptionResponse subscription;
    private boolean isAlive;
}
