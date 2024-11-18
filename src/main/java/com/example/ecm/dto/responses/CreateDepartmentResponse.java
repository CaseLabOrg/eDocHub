package com.example.ecm.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO департамента для ответа
 */
@Getter
@Setter
public class CreateDepartmentResponse {
    /**
     * Идентификатор департамента
     */
    private Long id;
    /**
     * Название департамента
     */
    private String name;
    /**
     * Главный по департаменту
     */
    private CreateUserResponse leader;
    /**
     * Участники департамента
     */
    private List<CreateUserResponse> members;
}
