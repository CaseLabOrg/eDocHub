package com.example.ecm.service;

import com.example.ecm.dto.*;
import com.example.ecm.mapper.*;
import com.example.ecm.model.*;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.DocumentTypeRepository;
import com.example.ecm.repository.DocumentVersionRepository;
import com.example.ecm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * Создает новый документ.
     * Сохраняет данные документа в базе данных и файл в MinIO.
     * В случае ошибки сохранения файла, документ удаляется из базы данных.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа или null в случае ошибки
     */
    public CreateDocumentResponse createDocument(CreateDocumentRequest createDocumentRequest) {


        DocumentVersion documentVersion = documentMapper.toDocumentVersion(createDocumentRequest);
        DocumentVersion documentVersionSaved = documentVersionRepository.save(documentVersion);

        User user = userRepository.findById(createDocumentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        DocumentType documentType = documentTypeRepository.findById(createDocumentRequest.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));

        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);


        Document documentSaved = documentRepository.save(document);
        CreateDocumentVersionRequest createDocumentVersionRequest = new CreateDocumentVersionRequest();

        createDocumentVersionRequest.setDescription(documentVersion.getDescription());
        createDocumentVersionRequest.setTitle(documentVersion.getTitle());
        //response.setValues();
        createDocumentVersionRequest.setBase64Content(createDocumentRequest.getBase64Content());


        boolean success = minioService.addDocument(documentVersionSaved.getId(), createDocumentVersionRequest);
        if (!success) {
            documentRepository.deleteById(documentVersionSaved.getId());
            return null;
        }

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(documentSaved, documentVersionSaved);
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
        Optional<Document> document = documentRepository.findById(id);
        List<DocumentVersion> list = document.get().getDocumentVersions();

        if(document.isPresent()){
            for(DocumentVersion documentVersion : list) {
                minioService.deleteDocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle());
            }
        }

        documentRepository.deleteById(id);
    }


    public CreateDocumentVersionResponse updateDocumentVersion(Long id, CreateDocumentVersionRequest createDocumentVersionRequest) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        DocumentVersion documentVersion = new DocumentVersion();

        documentVersion = documentVersionMapper.toDocumentVersion(createDocumentVersionRequest);
        documentVersion.setVersionId((long)document.getDocumentVersions().size());

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(documentVersion));

        minioService.addDocument(documentVersion.getId(), createDocumentVersionRequest);


        response.setBase64Content(createDocumentVersionRequest.getBase64Content());
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
}
