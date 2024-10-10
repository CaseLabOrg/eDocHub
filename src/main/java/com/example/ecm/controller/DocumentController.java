package com.example.ecm.controller;



import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.model.Document;
import com.example.ecm.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;


    @PostMapping
    public ResponseEntity<CreateDocumentResponse> createFile(@Valid @RequestBody CreateDocumentRequest createDocumentRequest){
        CreateDocumentResponse documentResponse = documentService.createDocument(createDocumentRequest);
        return ResponseEntity.ok(documentResponse);
    }

    @GetMapping("/{id}")
    private ResponseEntity<CreateDocumentResponse> getDocument(@PathVariable Long id){
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentResponse> updateDocument(@PathVariable Long id, @Valid @RequestBody CreateDocumentRequest document){
        return ResponseEntity.ok(documentService.updateDocument(id, document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@Valid @RequestBody Long id){
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
