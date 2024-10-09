package com.example.ecm.mapper;

import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.model.DocumentType;

public class DocumentTypeMapper {
    public DocumentType toDocumentType(CreateDocumentTypeRequest request) {
        DocumentType documentType = new DocumentType();
        documentType.setName(request.getName());
        return documentType;
    }

    public CreateDocumentTypeResponse toCreateDocumentTypeResponse(DocumentType documentType) {
        CreateDocumentTypeResponse createDocumentTypeResponse = new CreateDocumentTypeResponse();
        createDocumentTypeResponse.setId(documentType.getId());
        createDocumentTypeResponse.setName(documentType.getName());
        return createDocumentTypeResponse;
    }
}
