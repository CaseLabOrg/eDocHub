package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.dto.requests.SetValueRequest;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DocumentVersionMapper {
    private final SignatureMapper signatureMapper;

    public DocumentVersion toDocumentVersion(CreateDocumentVersionRequest request) {
        DocumentVersion documentVersion = new DocumentVersion();

        documentVersion.setTitle(request.getTitle());
        documentVersion.setDescription(request.getDescription());

        return documentVersion;
    }

    public CreateDocumentVersionResponse toCreateDocumentVersionResponse(DocumentVersion documentVersion) {
        CreateDocumentVersionResponse response = new CreateDocumentVersionResponse();

        response.setDescription(documentVersion.getDescription());
        response.setTitle(documentVersion.getTitle());
        response.setId(documentVersion.getId());
        response.setVersionId(documentVersion.getVersionId());
        response.setCreatedAt(documentVersion.getCreatedAt());
        response.setSignatures(documentVersion.getSignatures().stream()
                .map(signatureMapper::toGetSignatureResponse)
                .toList());
        response.setValues(documentVersion.getValues().entrySet().stream()
                .map(entry -> {
                    SetValueRequest setValueRequest = new SetValueRequest();
                    setValueRequest.setAttributeName(entry.getKey().getName());
                    setValueRequest.setValue(entry.getValue().getValue());
                    return setValueRequest;
                })
                .toList());

        return response;
    }

    public CreateDocumentVersionRequest toCreateDocumentVersionRequest(DocumentVersion documentVersion, String base64Content) {
        CreateDocumentVersionRequest request = new CreateDocumentVersionRequest();

        request.setDescription(documentVersion.getDescription());
        request.setTitle(documentVersion.getTitle());
        request.setBase64Content(base64Content);
        request.setValues(documentVersion.getValues().entrySet().stream()
                .map(entry -> {
                    SetValueRequest setValueRequest = new SetValueRequest();
                    setValueRequest.setAttributeName(entry.getKey().getName());
                    setValueRequest.setValue(entry.getValue().getValue());
                    return setValueRequest;
                })
                .toList());

        return request;
    }


    public DocumentElasticsearch mapToElasticsearch(DocumentVersion documentVersion) {

        DocumentElasticsearch elasticDocument = new DocumentElasticsearch();
        elasticDocument.setDocumentVersionId(documentVersion.getId());
        elasticDocument.setDocumentTypeId(documentVersion.getDocument().getDocumentType().getId());
        elasticDocument.setUserId(documentVersion.getDocument().getId());
        elasticDocument.setTitle(documentVersion.getTitle());
        elasticDocument.setDescription(documentVersion.getDescription());
        elasticDocument.setCreatedAt(documentVersion.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
        elasticDocument.setIsAlive(documentVersion.getIsAlive());

        // Преобразование карты значений
        Map<String, String> values = documentVersion.getValues().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        entry -> entry.getValue().getValue()
                ));
        elasticDocument.setValues(values);

        return elasticDocument;
    }


}
