package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePlanResponse {
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Long maxUsers;
}