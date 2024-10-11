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

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final SignatureMapper signatureMapper;

    /**
     * Создает новый документ.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа
     */
    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {
        Document document = documentMapper.toDocument(createDocumentRequest);
        Document documentSaved = documentRepository.save(document);
        return documentMapper.toCreateDocumentResponse(documentSaved);
    }

    /**
     * Получает документ по идентификатору.
     *
     * @param id идентификатор документа
     * @return ответ с данными документа
     */
    public CreateDocumentResponse getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(documentMapper::toCreateDocumentResponse)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    /**
     * Получает все документы.
     *
     * @return список ответов с данными всех документов
     */
    public List<CreateDocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toCreateDocumentResponse)
                .toList();
    }

    /**
     * Удаляет документ по идентификатору.
     *
     * @param id идентификатор документа
     */
    public void deleteDocument(Long id) {
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
        Document documentUpdate = documentMapper.documentUpdate(document);
        return documentMapper.toCreateDocumentResponse(documentRepository.save(documentUpdate));
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
