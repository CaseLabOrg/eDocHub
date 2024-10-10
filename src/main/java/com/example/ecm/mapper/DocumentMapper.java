package com.example.ecm.mapper;


import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.model.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    public Document toDocument(CreateDocumentRequest request) {
        Document document  = new Document();
        document.setTitle(request.getTitle());
        document.setUser(request.getUser());
        document.setDocumentType(request.getDocumentType());
        document.setDescription(request.getDescription());
        document.setCreated_at(request.getCreated_at());
        document.setVersion(request.getVersion());
        return document;
    }

    public Document documentUpdate(Document request) {
        Document document  = new Document();
        document.setTitle(request.getTitle());
        document.setUser(request.getUser());
        document.setDocumentType(request.getDocumentType());
        document.setDescription(request.getDescription());
        document.setCreated_at(request.getCreated_at());
        document.setVersion(request.getVersion());
        return document;
    }
    public CreateDocumentResponse toCreateDocumentResponse(Document document) {
        CreateDocumentResponse createDocumentResponse = new CreateDocumentResponse();
        createDocumentResponse.setId(document.getId());
        createDocumentResponse.setTitle(document.getTitle());
        createDocumentResponse.setUser(document.getUser());
        createDocumentResponse.setDocumentType(document.getDocumentType());
        createDocumentResponse.setDescription(document.getDescription());
        createDocumentResponse.setCreated_at(document.getCreated_at());
        createDocumentResponse.setVersion(document.getVersion());
        createDocumentResponse.setValues(document.getValues());
        return createDocumentResponse;
    }
}
