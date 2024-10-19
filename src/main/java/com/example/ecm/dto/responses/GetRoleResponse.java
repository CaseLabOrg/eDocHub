package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для передачи данных о роли.
 */
@Getter
@Setter
public class GetRoleResponse {
    private long id;
    private String name;
}