package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateInvoiceRequest {

    @NotNull(message = "Subscription cannot be null")
    private Long subscriptionId;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be bigger than zero")
    private BigDecimal amount;
}
