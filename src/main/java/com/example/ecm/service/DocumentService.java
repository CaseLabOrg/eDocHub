package com.example.ecm.service;

import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.model.Document;
import com.example.ecm.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;


    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {
        Document document = documentMapper.toDocument(createDocumentRequest);
        Document documentSaved = documentRepository.save(document);
        return documentMapper.toCreateDocumentResponse(documentSaved);
    }

    public CreateDocumentResponse getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(documentMapper::toCreateDocumentResponse)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public List<CreateDocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toCreateDocumentResponse)
                .toList();
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    public CreateDocumentResponse updateDocument(Long id, CreateDocumentRequest createDocumentRequest) {
        Document document = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
        Document documentUpdate  = documentMapper.documentUpdate(document);
        return documentMapper.toCreateDocumentResponse(documentRepository.save(documentUpdate));
    }
}
