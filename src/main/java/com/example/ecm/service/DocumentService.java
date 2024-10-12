package com.example.ecm.service;

import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.dto.SignatureDto;
import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.mapper.SignatureMapper;
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
    private final SignatureMapper signatureMapper;
    private final MinioService minioService;

    /**
     * Создает новый документ.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа
     */
    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {
        Document document = documentMapper.toDocument(createDocumentRequest);
        Document documentSaved = documentRepository.save(document);
        boolean success = minioService.addDocument(documentSaved.getId(), createDocumentRequest);
        if (!success) {
            documentRepository.deleteById(documentSaved.getId());
            return null;
        }
        return documentMapper.toCreateDocumentResponse(documentSaved);
    }

    /**
     * Получает документ по идентификатору.
     *
     * @param id идентификатор документа
     * @return ответ с данными документа
     */
    public CreateDocumentResponse getDocumentById(Long id) {
        CreateDocumentResponse response = documentRepository.findById(id)
                .map(documentMapper::toCreateDocumentResponse)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        response.setBase64Content(minioService.getBase64DocumentByName(response.getId() + "_" + response.getTitle()));
        return response;
    }

    /**
     * Получает все документы.
     *
     * @return список ответов с данными всех документов
     */
    public List<CreateDocumentResponse> getAllDocuments() {
        List<CreateDocumentResponse> list = documentRepository.findAll().stream()
                .map(documentMapper::toCreateDocumentResponse)
                .toList();
        for (CreateDocumentResponse response : list) {
            response.setBase64Content(minioService.getBase64DocumentByName(response.getId() + "_" + response.getTitle()));
        }
        return list;
    }

    /**
     * Удаляет документ по идентификатору.
     *
     * @param id идентификатор документа
     */
    public void deleteDocument(Long id) {
        Optional<Document> document = documentRepository.findById(id);
        document.ifPresent(value -> minioService.deleteDocumentByName(value.getId() + "_" + value.getTitle()));
        documentRepository.deleteById(id);
    }

    /**
     * Обновляет документ по идентификатору.
     *
     * @param id идентификатор документа
     * @param createDocumentRequest запрос на обновление документа
     * @return ответ с данными обновленного документа
     */
    public CreateDocumentResponse updateDocument(Long id, CreateDocumentRequest createDocumentRequest) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        minioService.deleteDocumentByName(document.getId() + "_" + document.getTitle());
        document.setTitle(createDocumentRequest.getTitle());
        document.setUser(createDocumentRequest.getUser());
        document.setDocumentType(createDocumentRequest.getDocumentType());
        document.setDescription(createDocumentRequest.getDescription());
        document.setVersion(createDocumentRequest.getVersion());
        minioService.addDocument(id, createDocumentRequest);
        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(documentRepository.save(document));
        response.setBase64Content(createDocumentRequest.getBase64Content());
        return response;
    }

    /**
     * Добавляет подпись в документ.
     *
     * @param id идентификатор документа
     * @param signatureDto подпись
     */
    public void signDocument(Long id, SignatureDto signatureDto) {
        var document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        var signatures = document.getSignatures();
        signatures.add(signatureMapper.toSignature(signatureDto));
        document.setSignatures(signatures);
    }
}
