package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateSubscriptionResponse {
    private Long id;

    private CreatePlanResponse plan;

    private String status;

    private Long tenantId;

    private LocalDate startDate;

    private LocalDate endDate;
}