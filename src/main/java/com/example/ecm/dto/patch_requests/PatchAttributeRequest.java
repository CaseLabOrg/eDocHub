package com.example.ecm.dto.patch_requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatchAttributeRequest {
    /**
     * Название типа документа, к которому относится атрибут
     */
    private List<String> documentTypesNames;

    /**
     * Имя атрибута, которое описывает его назначение
     */
    private String name;


    private Boolean required;
}
