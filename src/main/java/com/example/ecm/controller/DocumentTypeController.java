package com.example.ecm.controller;

import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.model.User;
import com.example.ecm.service.DocumentTypeService;
import com.example.ecm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/document-types")
public class DocumentTypeController {
    private final DocumentTypeService documentTypeService;

    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @PostMapping
    public ResponseEntity<CreateDocumentTypeResponse> createDocumentType(@RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.createDocumentType(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> getDocumentTypeById(@PathVariable Long id) {
        return documentTypeService.getDocumentTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CreateDocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeService.getAllDocumentTypes();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateDocumentTypeResponse> updateDocumentType(@PathVariable Long id, @RequestBody CreateDocumentTypeRequest request) {
        return ResponseEntity.ok(documentTypeService.updateDocumentType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentTypeById(@PathVariable Long id) {
        documentTypeService.deleteDocumentType(id);
        return ResponseEntity.noContent().build();
    }
}
