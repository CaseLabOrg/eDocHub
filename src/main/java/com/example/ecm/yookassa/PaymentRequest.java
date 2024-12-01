package com.example.ecm.yookassa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Amount amount;
    private boolean capture;
    private PaymentMethodData payment_method_data;
    private Confirmation confirmation;
    private String description;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Amount {
        private String value;
        private String currency;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PaymentMethodData {
        private String type;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Confirmation {
        private String type;
        private String return_url;
    }
}