package com.example.ecm.mapper;


import com.example.ecm.dto.CreateDocumentVersionRequest;
import com.example.ecm.dto.CreateDocumentVersionResponse;
import com.example.ecm.dto.SetValueRequest;
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
        
        return documentVersion;
    }

    public CreateDocumentVersionResponse toCreateDocumentVersionResponse(DocumentVersion documentVersion) {
        CreateDocumentVersionResponse response = new CreateDocumentVersionResponse();

        response.setDescription(documentVersion.getDescription());
        response.setTitle(documentVersion.getTitle());
        response.setId(documentVersion.getId());
        response.setVersionId(documentVersion.getVersionId());
        response.setCreatedAt(documentVersion.getCreatedAt());
        //response.setSignatures(documentVersion.getSignatures().stream()
         //       .map(signatureMapper::fromSignature)
        //        .toList());
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


}
