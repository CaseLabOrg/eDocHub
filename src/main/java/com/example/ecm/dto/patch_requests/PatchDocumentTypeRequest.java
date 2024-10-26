package com.example.ecm.dto.patch_requests;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatchDocumentTypeRequest {

    /**
     * Название типа документа.
     */
    private String name;
}
