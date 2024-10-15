package com.example.ecm.service;

import com.example.ecm.dto.CreateDocumentRequest;
import com.example.ecm.dto.CreateDocumentResponse;
import com.example.ecm.dto.SignatureDto;
import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.Signature;
import com.example.ecm.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с документами.
 * Обеспечивает создание, получение, обновление и удаление документов.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final SignatureMapper signatureMapper;
    private final MinioService minioService;

    /**
     * Создает новый документ.
     * Сохраняет данные документа в базе данных и файл в MinIO.
     * В случае ошибки сохранения файла, документ удаляется из базы данных.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа или null в случае ошибки
     */
    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {
        Document document = documentMapper.toDocument(createDocumentRequest);
        Document documentSaved = documentRepository.save(document);
        boolean success = minioService.addDocument(documentSaved.getId(), createDocumentRequest);
        if (!success) {
            documentRepository.deleteById(documentSaved.getId());
            return null;
        }
        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(documentRepository.save(document));
        response.setBase64Content(createDocumentRequest.getBase64Content());
        return response;
    }

    /**
     * Получает документ по его идентификатору.
     * Если документ существует, данные загружаются, включая содержимое файла в формате Base64.
     *
     * @param id идентификатор документа
     * @return ответ с данными документа
     * @throws RuntimeException если документ не найден
     */
    public CreateDocumentResponse getDocumentById(Long id) {
        CreateDocumentResponse response = documentRepository.findById(id)
                .map(documentMapper::toCreateDocumentResponse)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        response.setBase64Content(minioService.getBase64DocumentByName(response.getId() + "_" + response.getTitle()));
        return response;
    }

    /**
     * Получает список всех документов.
     * Каждый документ в списке включает данные и содержимое файла в формате Base64.
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
     * Удаляет документ по его идентификатору.
     * Удаляются как данные из базы, так и файл из MinIO.
     *
     * @param id идентификатор документа
     */
    public void deleteDocument(Long id) {
        Optional<Document> document = documentRepository.findById(id);
        document.ifPresent(value -> minioService.deleteDocumentByName(value.getId() + "_" + value.getTitle()));
        documentRepository.deleteById(id);
    }

    /**
     * Обновляет данные существующего документа.
     * Старое содержимое документа удаляется из MinIO, и загружается новое.
     *
     * @param id                    идентификатор документа
     * @param createDocumentRequest запрос на обновление документа
     * @return ответ с данными обновленного документа
     * @throws RuntimeException если документ не найден
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
     * @param id           идентификатор документа
     * @param signatureDto подпись
     */
    public void signDocument(Long id, SignatureDto signatureDto) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        List<Signature> signatures = document.getSignatures();
        signatures.add(signatureMapper.toSignature(signatureDto));
        document.setSignatures(signatures);
    }
}
