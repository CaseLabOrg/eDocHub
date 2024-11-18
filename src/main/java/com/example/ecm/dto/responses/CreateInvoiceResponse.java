package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateInvoiceResponse {
    private Long id;

    private CreateSubscriptionResponse subscription;

    private String description;

    private BigDecimal amount;

    private String status;

    private LocalDate createdDate;
}

