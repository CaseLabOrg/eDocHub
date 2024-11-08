package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchDocumentVersionRequest;
import com.example.ecm.dto.requests.AddCommentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.requests.SetValueRequest;
import com.example.ecm.dto.responses.AddCommentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.mapper.*;
import com.example.ecm.model.*;
import com.example.ecm.repository.*;

import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.exception.ServerException;
import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import com.example.ecm.parser.Base64Manager;
import com.example.ecm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final DocumentVersionRepository documentVersionRepository;
    private final AttributeRepository attributeRepository;
    private final ValueRepository valueRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
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
                .orElseThrow(() -> new NotFoundException("User with id: " + createDocumentRequest.getUserId() + " not found"));
        DocumentType documentType = documentTypeRepository.findById(createDocumentRequest.getDocumentTypeId())
                .orElseThrow(() -> new NotFoundException("Document type with id: " + createDocumentRequest.getDocumentTypeId() + " not found"));
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setIsAlive(true);
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
        createDocumentVersionRequest.setBase64Content(createDocumentRequest.getBase64Content());

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
                createDocumentRequest,
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
    public CreateDocumentResponse getDocumentById(Long id, Boolean showOnlyAlive, UserPrincipal userPrincipal) {
        Optional<Document> document = documentRepository.findById(id);

        if (showOnlyAlive) {
            document = document.filter(Document::getIsAlive);
        }

        if (!userPrincipal.isAdmin()) {
            document = document.filter(d -> d.getUser().getId().equals(userPrincipal.getId()));
        }

        Document doc = document.orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(doc);

        return getCreateDocumentResponse(doc, response);
    }

    public CreateDocumentVersionResponse getDocumentVersionById(Long documentId, Long versionId, Boolean showOnlyAlive) {
        Optional<DocumentVersion> documentVersion = documentVersionRepository.findByDocumentIdAndVersionId(documentId, versionId);

        if (showOnlyAlive) {
            documentVersion = documentVersion.filter(DocumentVersion::getIsAlive);
        }

        DocumentVersion version = documentVersion.orElseThrow(() -> new NotFoundException("Document Version with id: " + versionId + " or Document id " + documentId + " not found"));

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(version);
        String base64Content = minioService.getBase64DocumentByName(version.getId() + "_" + version.getTitle());
        response.setBase64Content(base64Content);
        return response;

    }

    /**
     * Получает список всех документов.
     * Каждый документ в списке включает данные и содержимое файла в формате Base64.
     *
     * @return список ответов с данными всех документов
     */
    public List<CreateDocumentResponse> getAllDocuments(Integer page, Integer size, Boolean ascending, Boolean showOnlyAlive, UserPrincipal userPrincipal) {

        List<DocumentVersion> latestVersions = documentVersionRepository.findLatestDocumentVersions();

        latestVersions.sort(Comparator.comparing(DocumentVersion::getCreatedAt)
                .reversed());

        List<Long> documentIds = latestVersions.stream()
                .map(version -> version.getDocument().getId())
                .distinct()
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, latestVersions.size());

        Stream<Document> documentStream = documentRepository.findAllById(documentIds.subList(start, end)).stream();

        if (showOnlyAlive) {
            documentStream = documentStream.filter(Document::getIsAlive);
        }

        if (!userPrincipal.isAdmin()) {
            documentStream = documentStream.filter(d -> d.getUser().getId().equals(userPrincipal.getId()));
        }

        List<CreateDocumentResponse> createDocumentResponses = documentStream
                .map(document -> {
                    CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document);
                    return getCreateDocumentResponse(document, response);
                })
                .toList();
        return new PageImpl<>(
                createDocumentResponses, PageRequest.of(page, size),
                latestVersions.size()
        ).getContent();
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

    public void deleteDocument(Long id) throws IOException {

        Document document = documentRepository.findById(id)
                .filter(Document::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        document.setIsAlive(false);
        documentRepository.save(document);
    }

    public void recoverDocument(Long id) throws IOException {
        Document document = documentRepository.findById(id)
                .filter(d -> !d.getIsAlive())
                .orElseThrow(() -> new NotFoundException("Deleted Document with id: " + id + " not found"));
        document.setIsAlive(true);
        documentRepository.save(document);
        searchService.recoverByDocumentVersionId(document.getDocumentVersions().getLast().getId());
    }

    /**
     * Создает новую версию документа и сохраняет её в базе данных.
     *
     * <p>Метод находит документ по указанному ID, создает новую версию документа,
     * задает ей порядковый номер, время создания и связь с документом. Далее версия
     * сохраняется в репозитории версий документов. Также сохраняются значения атрибутов
     * для этой версии и добавляется файл документа в MinIO.</p>
     *
     * @param id                           идентификатор документа, для которого создается новая версия
     * @param createDocumentVersionRequest объект {@link CreateDocumentVersionRequest}, содержащий информацию о новой версии
     * @return объект {@link CreateDocumentVersionResponse}, содержащий данные о созданной версии
     * @throws NotFoundException если документ с указанным ID не найден
     */
    public CreateDocumentVersionResponse updateDocumentVersion(Long id, CreateDocumentVersionRequest createDocumentVersionRequest) {
        Document document = documentRepository.findById(id)
                .filter(Document::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        DocumentVersion documentVersion = documentVersionMapper.toDocumentVersion(createDocumentVersionRequest);
        documentVersion.setVersionId((long) document.getDocumentVersions().size() + 1);
        documentVersion.setCreatedAt(LocalDateTime.now());
        documentVersion.setDocument(document);

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(documentVersion));

        setValues(createDocumentVersionRequest.getValues(), documentVersion);

        minioService.addDocument(documentVersion.getId(), createDocumentVersionRequest);
        response.setBase64Content(createDocumentVersionRequest.getBase64Content());
        response.setValues(createDocumentVersionRequest.getValues());
        return response;
    }

    /**
     * Сохраняет или обновляет значения атрибутов для указанной версии документа.
     *
     * <p>Метод проходит по списку значений атрибутов, находит соответствующие атрибуты в репозитории
     * и связывает их с версией документа. Новые значения атрибутов сохраняются в базе данных.</p>
     *
     * @param values          список значений атрибутов для обновления
     * @param documentVersion версия документа, для которой устанавливаются значения атрибутов
     * @throws NotFoundException если атрибут с указанным именем не найден
     */
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
     * Частично обновляет существующую версию документа на основе переданных изменений.
     *
     * <p>Метод находит версию документа по её ID и обновляет только те поля, которые переданы
     * в объекте {@link PatchDocumentVersionRequest}. Если передано новое содержимое (Base64),
     * оно загружается в MinIO, а старое удаляется. Также могут быть обновлены значения атрибутов.</p>
     *
     * @param id      идентификатор версии документа, которую требуется обновить
     * @param request объект {@link PatchDocumentVersionRequest}, содержащий данные для частичного обновления версии
     * @return объект {@link CreateDocumentVersionResponse}, содержащий обновленные данные о версии документа
     * @throws NotFoundException если версия документа с указанным ID не найдена
     */
    public CreateDocumentVersionResponse patchDocument(Long id, PatchDocumentVersionRequest request) {
        Document document= documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        DocumentVersion documentVersion = document.getDocumentVersions().getLast();

        if (request.getDescription() != null) {
            documentVersion.setDescription(request.getDescription());
        }
        if (request.getTitle() != null) {
            CreateDocumentVersionRequest requestDocumentVersion = documentVersionMapper.toCreateDocumentVersionRequest(documentVersion, minioService.getBase64DocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle()));
            documentVersion.setTitle(request.getTitle());
            requestDocumentVersion.setTitle(documentVersion.getTitle());
            minioService.addDocument(documentVersion.getId(), requestDocumentVersion);
        }
        if (request.getBase64Content() != null) {
            minioService.addDocument(documentVersion.getId(), documentVersionMapper.toCreateDocumentVersionRequest(documentVersion, request.getBase64Content()));
        }
        if (request.getValues() != null) {
            setValues(request.getValues(), documentVersion);
        }
        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(documentVersion));
        response.setBase64Content(minioService.getBase64DocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle()));

        documentVersionRepository.save(documentVersion);
        return response;

    }

    public AddCommentResponse addComment(Long id, AddCommentRequest addCommentRequest, UserPrincipal userPrincipal) {
        Comment comment = commentMapper.toComment(addCommentRequest);
        Document document = documentRepository.findById(id)
                .filter(Document::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        User author = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        comment.setDocument(document);
        comment.setAuthor(author);

        comment = commentRepository.save(comment);

        return commentMapper.toAddCommentResponse(comment);
    }
}
