package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreatePaymentResponse {
    private Long id;

    private String paymentId;

    private CreateInvoiceResponse invoice;

    private String paymentMethod;

    private String status;

    private LocalDateTime createdAt;

    private String paymentUrl;
}
