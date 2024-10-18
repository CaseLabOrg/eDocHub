package com.example.ecm.mapper;


import com.example.ecm.dto.CreateDocumentVersionRequest;
import com.example.ecm.dto.CreateDocumentVersionResponse;
import com.example.ecm.model.DocumentVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DocumentVersionMapper {
    private final SignatureMapper signatureMapper;

    public DocumentVersion toDocumentVersion(CreateDocumentVersionRequest request){
        DocumentVersion documentVersion = new DocumentVersion();

        documentVersion.setTitle(request.getTitle());
        documentVersion.setDescription(request.getDescription());
        documentVersion.setCreatedAt(LocalDateTime.now());
        
        return documentVersion;
    }

    public CreateDocumentVersionResponse toCreateDocumentVersionResponse(DocumentVersion documentVersion) {
        CreateDocumentVersionResponse response = new CreateDocumentVersionResponse();

        response.setCreatedAt(LocalDateTime.now());
        response.setDescription(documentVersion.getDescription());
        response.setTitle(documentVersion.getTitle());
        response.setId(documentVersion.getId());
        response.setVersionId(documentVersion.getVersionId());
        response.setSignatures(documentVersion.getSignatures().stream()
                .map(signatureMapper::fromSignature)
                .toList());

        return response;
    }


}
