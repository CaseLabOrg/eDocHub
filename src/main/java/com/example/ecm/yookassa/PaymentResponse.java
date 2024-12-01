package com.example.ecm.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {
    private String id;
    private String status;
    private Confirmation confirmation;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class Confirmation {
        @JsonProperty("confirmation_url")
        private String confirmationUrl;
    }
}
