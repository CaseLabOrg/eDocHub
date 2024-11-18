package com.example.ecm.kafka.event;

import com.example.ecm.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private Long invoiceId;

    private PaymentStatus status;
}
