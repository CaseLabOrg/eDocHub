package com.example.ecm.service;


import com.example.ecm.dto.CreateDocumentTypeRequest;
import com.example.ecm.dto.CreateDocumentTypeResponse;
import com.example.ecm.mapper.DocumentTypeMapper;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.DocumentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;

    DocumentTypeService(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
        this.documentTypeMapper = new DocumentTypeMapper();
    }

    public CreateDocumentTypeResponse createDocumentType(CreateDocumentTypeRequest request) {
        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentTypeMapper.toDocumentType(request)));
    }

    public Optional<CreateDocumentTypeResponse> getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .map(documentTypeMapper::toCreateDocumentTypeResponse);
    }

    public List<CreateDocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeRepository.findAll().stream()
                .map(documentTypeMapper::toCreateDocumentTypeResponse)
                .toList();
    }

    public CreateDocumentTypeResponse updateDocumentType(Long id, CreateDocumentTypeRequest request) {
        DocumentType documentType = documentTypeRepository.findById(id).orElseThrow();
        documentType.setId(id);
        documentType.setName(request.getName());
        return documentTypeMapper.toCreateDocumentTypeResponse(documentTypeRepository.save(documentType));
    }

    public void deleteDocumentType(Long id) {
        documentTypeRepository.deleteById(id);
    }

}
