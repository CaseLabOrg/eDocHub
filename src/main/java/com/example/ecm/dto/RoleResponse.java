package com.example.ecm.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для передачи данных о роли.
 */
@Getter
@Setter
public class RoleResponse {
    private long id;
    private String name;
}