package com.example.ecm.dto;

import lombok.Setter;

@Setter
public class CreateUserResponse {
    private Long id;

    private String name;

    private String surname;

    private String email;
}
