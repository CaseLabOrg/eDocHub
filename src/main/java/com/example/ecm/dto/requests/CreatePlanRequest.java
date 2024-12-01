package com.example.ecm.dto.requests;

import com.example.ecm.exception.validator.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePlanRequest {
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be bigger than zero")
    private BigDecimal price;

    @NotNull(message = "Max users cannot be null")
    @Positive(message = "Max users must be bigger than zero")
    private Long maxUsers;
}
