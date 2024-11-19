package com.example.ecm.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для запроса на создание департамента
 */
@Getter
@Setter
public class CreateDepartmentRequest {

    /**
     * Название департамента
     */
    @NotEmpty
    private String name;

    /**
     * Идентификатор главного по департаменту
     */
    @NotNull
    private Long leaderId;

    /**
     * Идентификатор родительского департамента
     */
    public Long parentId;
}
