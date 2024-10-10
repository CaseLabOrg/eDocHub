package com.example.ecm.dto;

import com.example.ecm.model.Attribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateDocumentTypeResponse {
    private Long id;
    private String name;
    private List<Attribute> attributes;
}
