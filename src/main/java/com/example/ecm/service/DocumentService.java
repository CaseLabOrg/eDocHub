package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchDocumentVersionRequest;
import com.example.ecm.dto.requests.AddCommentRequest;
import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.requests.SetValueRequest;
import com.example.ecm.dto.responses.AddCommentResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.dto.requests.CreateDocumentRequest;
import com.example.ecm.dto.responses.CreateDocumentResponse;
import com.example.ecm.exception.ConflictException;
import com.example.ecm.mapper.*;
import com.example.ecm.model.*;
import com.example.ecm.model.enums.DocumentState;
import com.example.ecm.repository.*;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final DocumentStateService documentStateService;
    private final SignatureRequestRepository signatureRequestRepository;

    /**
     * Создает новый документ.
     * Сохраняет данные документа в базе данных и файл в MinIO.
     * В случае ошибки сохранения файла, документ удаляется из базы данных.
     *
     * @param createDocumentRequest запрос на создание документа
     * @return ответ с данными созданного документа или null в случае ошибки
     */
    @Transactional
    public CreateDocumentResponse createDocument(Boolean isDraft, CreateDocumentRequest createDocumentRequest) {
        User user = userRepository.findById(createDocumentRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User with id: " + createDocumentRequest.getUserId() + " not found"));
        DocumentType documentType = documentTypeRepository.findById(createDocumentRequest.getDocumentTypeId())
                .orElseThrow(() -> new NotFoundException("Document type with id: " + createDocumentRequest.getDocumentTypeId() + " not found"));
        Document document = new Document();
        document.setUser(user);
        document.setDocumentType(documentType);
        document.setIsAlive(true);
        if(isDraft)
            document.setState(DocumentState.DRAFT);
        Document documentSaved = documentRepository.save(document);

        DocumentVersion documentVersion = documentMapper.toDocumentVersion(createDocumentRequest);
        documentVersion.setDocument(documentSaved);
        documentVersion.setVersionId(1L);
        documentVersion.setCreatedAt(LocalDateTime.now());
        String filename = minioService.parseFilename(createDocumentRequest.getBase64Content());
        filename = filename != null ? filename : "Untitled";
        documentVersion.setFilename(filename);
        DocumentVersion documentVersionSaved = documentVersionRepository.save(documentVersion);

        setValues(createDocumentRequest.getValues(), documentVersionSaved);

        CreateDocumentVersionRequest createDocumentVersionRequest = new CreateDocumentVersionRequest();

        createDocumentVersionRequest.setDescription(documentVersion.getDescription());
        createDocumentVersionRequest.setTitle(documentVersion.getTitle());
        createDocumentVersionRequest.setBase64Content(createDocumentRequest.getBase64Content());

        if (!minioService.addDocument(documentVersionSaved.getId(), createDocumentVersionRequest)) {
            documentRepository.deleteById(documentVersionSaved.getId());
            throw new ServerException("Could not add document");
        }

        List<CreateDocumentVersionResponse> documentVersions = new ArrayList<>();
        CreateDocumentVersionResponse createDocumentVersionResponse = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionSaved);
        createDocumentVersionResponse.setBase64Content(createDocumentRequest.getBase64Content());
        createDocumentVersionResponse.setValues(createDocumentRequest.getValues());
        documentVersions.add(createDocumentVersionResponse);

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
    public CreateDocumentResponse getDocumentById(Long id, Boolean isAlive, UserPrincipal userPrincipal) {
        Optional<Document> document = documentRepository.findById(id);

        if (isAlive) {
            document = document.filter(Document::getIsAlive);
        } else {
            document = document.filter(d -> !d.getIsAlive());
        }

        if (!userPrincipal.isAdmin()) {
            document = document
                    .filter(d ->
                            Objects.equals(d.getUser().getId(), userPrincipal.getId())
                                    ||
                                    d.getDocumentVersions().stream()
                                            .anyMatch(version ->
                                                    signatureRequestRepository.existsByUserToIdAndDocumentVersionId(
                                                            userPrincipal.getId(), version.getId()
                                                    )
                                            ));
        }

        Document doc = document.orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(doc);

        return getCreateDocumentResponse(doc, response, userPrincipal);
    }

    public CreateDocumentVersionResponse getDocumentVersionById(Long documentId, Long versionId, Boolean isAlive, UserPrincipal userPrincipal) {
        Optional<DocumentVersion> documentVersion = documentVersionRepository.findByDocumentIdAndVersionId(documentId, versionId);

        if (isAlive) {
            documentVersion = documentVersion.filter(v -> v.getDocument().getIsAlive());
        } else {
            documentVersion = documentVersion.filter(v -> !v.getDocument().getIsAlive());
        }

        if (!userPrincipal.isAdmin()) {
            documentVersion = documentVersion.filter(version ->
                    Objects.equals(version.getDocument().getUser().getId(), userPrincipal.getId())
                            ||
                            signatureRequestRepository.existsByUserToIdAndDocumentVersionId(userPrincipal.getId(), version.getId()));
        }

        DocumentVersion version = documentVersion.orElseThrow(() -> new NotFoundException("Document Version with id: " + versionId + " or Document id " + documentId + " not found"));

        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(version);
        String base64Content = minioService.getBase64DocumentByName(version.getId() + "_" + version.getFilename());
        response.setBase64Content(base64Content);
        return response;

    }

    /**
     * Получает список всех документов.
     * Каждый документ в списке включает данные и содержимое файла в формате Base64.
     *
     * @return список ответов с данными всех документов
     */
    public List<CreateDocumentResponse> getAllDocuments(Integer page, Integer size, Boolean ascending, Boolean isAlive, UserPrincipal userPrincipal, Boolean showDraft) {

        List<DocumentVersion> latestVersions = documentVersionRepository.findLatestDocumentVersions();

        latestVersions.sort(Comparator.comparing(DocumentVersion::getCreatedAt)
                .reversed());

        List<Long> documentIds = latestVersions.stream()
                .map(version -> version.getDocument().getId())
                .distinct()
                .collect(Collectors.toList());

        Stream<Document> documentStream = documentRepository.findAllById(documentIds).stream();


        if (isAlive) {
            documentStream = documentStream.filter(Document::getIsAlive);
        } else {
            documentStream = documentStream.filter(d -> !d.getIsAlive());
        }


        if (!userPrincipal.isAdmin()) {
            documentStream = documentStream
                    .filter(document ->
                            Objects.equals(document.getUser().getId(), userPrincipal.getId())
                                    ||
                                    document.getDocumentVersions().stream()
                                            .anyMatch(version ->
                                                    signatureRequestRepository.existsByUserToIdAndDocumentVersionId(
                                                            userPrincipal.getId(), version.getId()
                                                    )
                                            ));
        }

        if (showDraft != null) {
            if (showDraft) {
                documentStream = documentStream.filter(document -> document.getState() == DocumentState.DRAFT);
            } else {
                documentStream = documentStream.filter(document -> document.getState() != DocumentState.DRAFT);
            }
        }

        List<Document> filteredDocuments = documentStream.toList();


        int start = page * size;
        int end = Math.min(start + size, filteredDocuments.size());
        List<Document> paginatedDocuments = filteredDocuments.subList(start, end);


        List<CreateDocumentResponse> createDocumentResponses = paginatedDocuments.stream()
                .map(document -> {
                    CreateDocumentResponse response = documentMapper.toCreateDocumentResponse(document);
                    return getCreateDocumentResponse(document, response, userPrincipal);
                })
                .toList();

        return new PageImpl<>(
                createDocumentResponses, PageRequest.of(page, size),
                filteredDocuments.size()
        ).getContent();
    }

    public int getCountDocuments(Boolean showOnlyAlive, UserPrincipal userPrincipal, Boolean showDraft) {

        List<DocumentVersion> latestVersions = documentVersionRepository.findLatestDocumentVersions();

        latestVersions.sort(Comparator.comparing(DocumentVersion::getCreatedAt)
                .reversed());

        List<Long> documentIds = latestVersions.stream()
                .map(version -> version.getDocument().getId())
                .distinct()
                .collect(Collectors.toList());


        Stream<Document> documentStream = documentRepository.findAllById(documentIds.subList(0, latestVersions.size())).stream();

        if (showOnlyAlive) {
            documentStream = documentStream.filter(Document::getIsAlive);
        } else {
            documentStream = documentStream.filter(document -> document.getIsAlive().equals(Boolean.FALSE));
        }
        if (showDraft != null) {
            if (showDraft) {
                documentStream = documentStream.filter(document -> document.getState() == DocumentState.DRAFT);
            } else {
                documentStream = documentStream.filter(document -> document.getState() != DocumentState.DRAFT);
            }
        }

        if (!userPrincipal.isAdmin()) {
            documentStream = documentStream
                    .filter(document ->
                            Objects.equals(document.getUser().getId(), userPrincipal.getId())
                                    ||
                                    document.getDocumentVersions().stream()
                                            .anyMatch(version ->
                                                    signatureRequestRepository.existsByUserToIdAndDocumentVersionId(
                                                            userPrincipal.getId(), version.getId()
                                                    )
                                            ));
        }

        return documentStream.toList().size();

    }


    private CreateDocumentResponse getCreateDocumentResponse(Document document, CreateDocumentResponse response, UserPrincipal userPrincipal) {
        Stream<DocumentVersion> documentStream = document.getDocumentVersions().stream();

        response.setDocumentVersions(documentStream
                .map(version -> {
                    CreateDocumentVersionResponse versionResponse = documentVersionMapper.toCreateDocumentVersionResponse(version);
                    String base64Content = minioService.getBase64DocumentByName(version.getId() + "_" + version.getFilename());
                    versionResponse.setBase64Content(base64Content);
                    return versionResponse;
                }).toList());
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
                .filter(Document::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        if (!documentStateService.checkTransition(document, DocumentState.DELETED)) {
            throw new ConflictException("You cannot delete document with id: " + id + " check available transitions");
        }
        document.setState(DocumentState.DELETED);
        document.setIsAlive(false);
        documentRepository.save(document);
    }

    public void recoverDocument(Long id) {
        Document document = documentRepository.findById(id)
                .filter(d -> !d.getIsAlive())
                .orElseThrow(() -> new NotFoundException("Deleted Document with id: " + id + " not found"));
        if (!documentStateService.checkTransition(document, DocumentState.DRAFT)) {
            throw new ConflictException("You cannot recover document with id: " + id + " check available transitions");
        }
        document.setState(DocumentState.DRAFT);
        document.setIsAlive(true);
        documentRepository.save(document);
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
    @Transactional
    public CreateDocumentVersionResponse updateDocumentVersion(Long id, CreateDocumentVersionRequest createDocumentVersionRequest) {
        Document document = documentRepository.findById(id)
                .filter(Document::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));

        DocumentVersion lastDocumentVersion = document.getDocumentVersions().getLast();

        if (!documentStateService.checkTransition(document, DocumentState.MODIFIED)) {
            throw new ConflictException("You cannot modify document with id: " + id + " check available transitions");
        }

        DocumentVersion documentVersion = documentVersionMapper.toDocumentVersion(createDocumentVersionRequest);
        documentVersion.setVersionId((long) document.getDocumentVersions().size() + 1);
        documentVersion.setCreatedAt(LocalDateTime.now());
        documentVersion.setFilename(lastDocumentVersion.getFilename());
        document.setState(DocumentState.MODIFIED);


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
        Map<Attribute, Value> currentValues =  documentVersion.getValues();
        for (SetValueRequest newValue : values) {
            Attribute attribute = attributeRepository.findByName(newValue.getAttributeName())
                    .orElseThrow(() -> new NotFoundException("Attribute with name: " + newValue.getAttributeName() + " not found"));
            Value value = new Value();
            value.setAttribute(attribute);
            value.setDocumentVersion(documentVersion);
            value.setValue(newValue.getValue());
            value = valueRepository.save(value);
            currentValues.put(attribute, value);
        }
        documentVersion.setValues(currentValues);
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
    @Transactional
    public CreateDocumentVersionResponse patchDocument(Long id, Boolean isDone, PatchDocumentVersionRequest request) {
        Document document = documentRepository.findById(id).orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        if (!documentStateService.checkTransition(document, DocumentState.MODIFIED) && !document.getState().equals(DocumentState.DRAFT)) {
            throw new ConflictException("You cannot modify document with id: " + id + " check available transitions");
        }

        DocumentVersion documentVersion = document.getDocumentVersions().getLast();

        DocumentVersion newVersion2 = new DocumentVersion();
        if(!document.getState().equals(DocumentState.DRAFT)) {
            document.setState(DocumentState.MODIFIED);
            newVersion2.setDocument(document);
            newVersion2.setVersionId(documentVersion.getVersionId() + 1);
            newVersion2.setDescription(documentVersion.getDescription());
            newVersion2.setCreatedAt(LocalDateTime.now());
            newVersion2.setFilename(documentVersion.getFilename());
            newVersion2.setIsAlive(true);
        }
        else {
            if(isDone)
                document.setState(DocumentState.CREATED);
            newVersion2 = documentVersion;
        }

        if (request.getTitle() != null) {
            newVersion2.setTitle(request.getTitle());
        } else {
            newVersion2.setTitle(documentVersion.getTitle());
        }

        setValues(documentVersion.getValues().entrySet().stream()
                .map(entry -> {
                    SetValueRequest setValueRequest = new SetValueRequest();
                    setValueRequest.setAttributeName(entry.getKey().getName());
                    setValueRequest.setValue(entry.getValue().getValue());
                    return setValueRequest;
                }).toList(), newVersion2);



        DocumentVersion newVersion = documentVersionRepository.save(newVersion2);
        newVersion = documentVersionRepository.findById(newVersion.getId()).orElse(null);


        if (request.getDescription() != null) {
            newVersion.setDescription(request.getDescription());
        }
        if (request.getTitle() != null) {
            CreateDocumentVersionRequest requestDocumentVersion = documentVersionMapper.toCreateDocumentVersionRequest(newVersion, minioService.getBase64DocumentByName(documentVersion.getId() + "_" + documentVersion.getFilename()));
            newVersion.setTitle(request.getTitle());
            requestDocumentVersion.setTitle(newVersion.getTitle());
        }

        if (request.getBase64Content() != null && !request.getBase64Content().isEmpty()) {
            minioService.addDocument(newVersion.getId(), documentVersionMapper.toCreateDocumentVersionRequest(newVersion, request.getBase64Content()));
        } else {
            minioService.addDocument(newVersion.getId(), documentVersionMapper.toCreateDocumentVersionRequest(newVersion, minioService.getBase64DocumentByName(documentVersion.getId() + "_" + newVersion.getFilename())));
        }

        if (request.getValues() != null) {
            setValues(request.getValues(), newVersion);
        }
        CreateDocumentVersionResponse response = documentVersionMapper.toCreateDocumentVersionResponse(documentVersionRepository.save(newVersion));
        response.setBase64Content(minioService.getBase64DocumentByName(newVersion.getId() + "_" + newVersion.getFilename()));

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
