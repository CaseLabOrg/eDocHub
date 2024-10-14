package com.example.ecm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * DTO для передачи данных о роли в запросе.
 */
@Getter
public class RoleRequest {
    @NotNull
    private String roleName;
}