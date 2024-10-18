package com.example.ecm.service;

import com.example.ecm.dto.*;
import com.example.ecm.mapper.*;
import com.example.ecm.model.*;
import com.example.ecm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с документами.
 * Обеспечивает создание, получение, обновление и удаление документов.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentVersionMapper documentVersionMapper;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final MinioService minioService;
    private final DocumentVersionRepository documentVersionRepository;
    private final AttributeRepository attributeRepository;
    private final ValueRepository valueRepository;

    /**
     * Создает новый документ.
     * Сохраняет данные документа в базе данных и файл в MinIO.
     * В случае ошибки сохранения файла, документ удаляется из базы данных.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа или null в случае ошибки
     */
    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {
        User user = userRepository.findById(createDocumentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        DocumentType documentType = documentTypeRepository.findById(createDocumentRequest.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);

        Document documentSaved = documentRepository.save(document);

        DocumentVersion documentVersion = documentMapper.toDocumentVersion(createDocumentRequest);
        documentVersion.setDocument(documentSaved);
        documentVersion.setVersionId(1L);
        DocumentVersion documentVersionSaved = documentVersionRepository.save(documentVersion);

        setValues(createDocumentRequest.getValues(), documentVersionSaved);

        CreateDocumentVersionRequest createDocumentVersionRequest = new CreateDocumentVersionRequest();

        createDocumentVersionRequest.setDescription(documentVersion.getDescription());
        createDocumentVersionRequest.setTitle(documentVersion.getTitle());
        createDocumentVersionRequest.setBase64Content(createDocumentRequest.getBase64Content());

        boolean success = minioService.addDocument(documentVersionSaved.getId(), createDocumentVersionRequest);
        if (!success) {
            documentRepository.deleteById(documentVersionSaved.getId());
            throw new RuntimeException("Could not add document");
        }

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(documentSaved, documentVersionSaved);
        response.setBase64Content(createDocumentRequest.getBase64Content());
        response.setVersionId(documentVersion.getVersionId());
        response.setValues(createDocumentRequest.getValues());
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
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        DocumentVersion documentVersion = documentVersionRepository.findById(document.getDocumentVersions().getLast().getId()).
                orElseThrow(() -> new RuntimeException("Document not found"));

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document, documentVersion);

        response.setBase64Content(minioService.getBase64DocumentByName(documentVersion.getId() + "_" + response.getTitle()));
        return response;

    }

    /**
     * Получает список всех документов.
     * Каждый документ в списке включает данные и содержимое файла в формате Base64.
     *
     * @return список ответов с данными всех документов
     */
    public List<CreateDocumentResponse> getAllDocuments() {

            return documentRepository.findAll().stream()
                    .map(document -> {
                        DocumentVersion documentVersion = document.getDocumentVersions().getLast();

                        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document, documentVersion);

                        response.setBase64Content(minioService.getBase64DocumentByName(documentVersion.getId() + "_" + response.getTitle()));

                        return response;
                    })
                    .toList();
        }



    /**
     * Удаляет документ по его идентификатору.
     * Удаляются как данные из базы, так и файл из MinIO.
     *
     * @param id идентификатор документа
     */
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        List<DocumentVersion> list = document.getDocumentVersions();

        for(DocumentVersion documentVersion : list) {
            minioService.deleteDocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle());
        }
        documentRepository.deleteById(id);
    }


    public CreateDocumentVersionResponse updateDocumentVersion(Long id, CreateDocumentVersionRequest createDocumentVersionRequest) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        DocumentVersion documentVersion = documentVersionMapper.toDocumentVersion(createDocumentVersionRequest);
        documentVersion.setVersionId((long)document.getDocumentVersions().size());

        setValues(createDocumentVersionRequest.getValues(), documentVersion);

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(documentVersion));
        minioService.addDocument(documentVersion.getId(), createDocumentVersionRequest);
        response.setBase64Content(createDocumentVersionRequest.getBase64Content());
        response.setValues(createDocumentVersionRequest.getValues());
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
     //   List<Signature> signatures = document.getSignatures();
      //  signatures.add(signatureMapper.toSignature(signatureDto));
     //   document.setSignatures(signatures);
    }

    public void setValues(List<SetValueRequest> values, DocumentVersion documentVersion) {
        for (SetValueRequest newValue : values) {
            Attribute attribute = attributeRepository.findByName(newValue.getAttributeName())
                    .orElseThrow(() -> new RuntimeException("Attribute not found"));
            Value value = new Value();
            value.setAttribute(attribute);
            value.setDocumentVersion(documentVersion);
            value.setValue(newValue.getValue());
            valueRepository.save(value);
        }
    }
}
