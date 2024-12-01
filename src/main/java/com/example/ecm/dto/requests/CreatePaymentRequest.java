package com.example.ecm.dto.requests;

import com.example.ecm.enums.PaymentMethod;
import com.example.ecm.exception.validator.EnumValidator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {
    @NotNull(message = "Invoice id cannot be null")
    private Long invoiceId;

    @EnumValidator(enumClass = PaymentMethod.class, message = "Incorrect payment method")
    private String paymentMethod;
}
