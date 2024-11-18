package com.example.ecm.kafka.event;

import com.example.ecm.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionStatusUpdateEvent {
    private Long subscriptionId;

    private SubscriptionStatus status;
}