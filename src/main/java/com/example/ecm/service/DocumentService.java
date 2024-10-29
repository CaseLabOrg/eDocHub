package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.requests.SetValueRequest;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.exception.ServerException;
import com.example.ecm.mapper.*;
import com.example.ecm.model.*;

import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import com.example.ecm.parser.Base64Manager;
import com.example.ecm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с документами.
 * Обеспечивает создание, получение, обновление и удаление документов.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentVersionMapper documentVersionMapper;
    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final DocumentVersionRepository documentVersionRepository;
    private final AttributeRepository attributeRepository;
    private final ValueRepository valueRepository;
    private final SignatureMapper signatureMapper;
    private final SearchService searchService;
    private final Base64Manager base64Manager;

    private final UserService userService;


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
                .orElseThrow(() -> new NotFoundException("User with id: " + createDocumentRequest.getUserId() +" not found"));
        DocumentType documentType = documentTypeRepository.findById(createDocumentRequest.getDocumentTypeId())
                .orElseThrow(() -> new NotFoundException("Document type with id: " + createDocumentRequest.getDocumentTypeId() +" not found"));
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        Document documentSaved = documentRepository.save(document);

        DocumentVersion documentVersion = documentMapper.toDocumentVersion(createDocumentRequest);
        documentVersion.setDocument(documentSaved);
        documentVersion.setVersionId(1L);
        documentVersion.setCreatedAt(LocalDateTime.now());
        DocumentVersion documentVersionSaved = documentVersionRepository.save(documentVersion);

        setValues(createDocumentRequest.getValues(), documentVersionSaved);

        CreateDocumentVersionRequest createDocumentVersionRequest = new CreateDocumentVersionRequest();

        createDocumentVersionRequest.setDescription(documentVersion.getDescription());
        createDocumentVersionRequest.setTitle(documentVersion.getTitle());
        createDocumentVersionRequest.setBase64Content(base64Manager.removeMetadataPrefix(createDocumentRequest.getBase64Content()));

        boolean success = minioService.addDocument(documentVersionSaved.getId(), createDocumentVersionRequest);
        if (!success) {
            documentRepository.deleteById(documentVersionSaved.getId());
            throw new ServerException("Could not add document");
        }

        List<CreateDocumentVersionResponse> documentVersions = new ArrayList<>();
        CreateDocumentVersionResponse createDocumentVersionResponse = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionSaved);
        createDocumentVersionResponse.setBase64Content(createDocumentRequest.getBase64Content());
        createDocumentVersionResponse.setValues(createDocumentRequest.getValues());
        documentVersions.add(createDocumentVersionResponse);

        // There add to elastic
        searchService.addIndexDocumentElasticsearch(
                DocumentMapper.toDocumentElasticsearch(createDocumentRequest),
                createDocumentRequest.getBase64Content(),
                documentVersionSaved.getId()
        );

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(documentSaved);
        response.setDocumentVersions(documentVersions);
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
                .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document);

        return getCreateDocumentResponse(document, response);

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
                        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document);

                        return getCreateDocumentResponse(document, response);
                    })
                    .toList();

        }

    private CreateDocumentResponse getCreateDocumentResponse(Document document, CreateDocumentResponse response) {
        response.setDocumentVersions(document.getDocumentVersions().stream()
                .map(version -> {
                    CreateDocumentVersionResponse versionResponse = documentVersionMapper.toCreateDocumentVersionResponse(version);
                    String base64Content = minioService.getBase64DocumentByName(version.getId() + "_" + version.getTitle());
                    versionResponse.setBase64Content(base64Content);
                    return versionResponse;
                })
                .toList());

        return response;
    }


    /**
     * Удаляет документ по его идентификатору.
     * Удаляются как данные из базы, так и файл из MinIO.
     *
     * @param id идентификатор документа
     */
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));
        List<DocumentVersion> list = document.getDocumentVersions();
        documentRepository.delete(document);
        for(DocumentVersion documentVersion : list) {
            minioService.deleteDocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle());
        }
    }


    public CreateDocumentVersionResponse updateDocumentVersion(Long id, CreateDocumentVersionRequest createDocumentVersionRequest) {
        Document document = documentRepository.findById(id)

                .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));

        DocumentVersion documentVersion = documentVersionMapper.toDocumentVersion(createDocumentVersionRequest);
        documentVersion.setVersionId((long)document.getDocumentVersions().size() + 1);
        documentVersion.setCreatedAt(LocalDateTime.now());
        documentVersion.setDocument(document);

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(documentVersion));

        setValues(createDocumentVersionRequest.getValues(), documentVersion);

        minioService.addDocument(documentVersion.getId(), createDocumentVersionRequest);
        response.setBase64Content(createDocumentVersionRequest.getBase64Content());
        response.setValues(createDocumentVersionRequest.getValues());
        return response;
    }

    private void setValues(List<SetValueRequest> values, DocumentVersion documentVersion) {
        for (SetValueRequest newValue : values) {
            Attribute attribute = attributeRepository.findByName(newValue.getAttributeName())
                    .orElseThrow(() -> new NotFoundException("Attribute with name: " + newValue.getAttributeName() + " not found"));
            Value value = new Value();
            value.setAttribute(attribute);
            value.setDocumentVersion(documentVersion);
            value.setValue(newValue.getValue());
            valueRepository.save(value);
        }
    }

    /**
     * Добавляет подпись в документ.
     *
     * @param id           идентификатор документа
     */
    /*
    public void signDocument(Long id, SignatureDto signatureDto) {
        DocumentVersion documentVersion = documentVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document version not found"));
        List<Signature> signatures = documentVersion.getSignatures();
        signatures.add(signatureMapper.toSignature(signatureDto));
        documentVersion.setSignatures(signatures);

    }
    */

    private User getUser(Long id) {
        return userService.findById(id).orElseThrow(() -> new NotFoundException("No such user"));
    }

    private DocumentType getDocumentType(Long id) {
        return documentTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("No such document type"));
    }
}
