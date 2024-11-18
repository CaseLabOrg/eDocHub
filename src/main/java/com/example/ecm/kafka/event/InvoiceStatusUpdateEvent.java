package com.example.ecm.kafka.event;

import com.example.ecm.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceStatusUpdateEvent {
    private Long invoiceId;

    private InvoiceStatus status;
}
