package com.example.ecm.dto.requests;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetValueRequest {
    private String attributeName;
    private String value;
}
