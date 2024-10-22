package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.*;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.DocumentVersionRepository;
import com.example.ecm.repository.SignatureRequestRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final UserRepository userRepository;
    private final SignatureMapper signatureMapper;

    public CreateSignatureRequestResponse sendToSign(Long id, CreateSignatureRequestRequest request, UserPrincipal currentUser) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));

        DocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(v -> v.getVersionId().equals(request.getDocumentVersionId()))
                .findFirst().orElseThrow(() -> new NotFoundException("Document version with id: " + id +" not found"));

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));

        if (!currentUser.getId().equals(document.getUser().getId()) || !currentUser.isAdmin()) {
            throw new ForbiddenException("You have no permission to send this document");
        }

        Signature signature = signatureMapper.toSignature(document, documentVersion);

        documentVersion.getSignatures().add(signature);

        documentVersionRepository.save(documentVersion);

        SignatureRequest signatureRequest = new SignatureRequest();
        signatureRequest.setUserTo(user);
        signatureRequest.setDocumentVersion(documentVersion);
        signatureRequest.setStatus("PENDING");

        signatureRequest = signatureRequestRepository.save(signatureRequest);

        return signatureMapper.toCreateSignatureRequestResponse(signatureRequest);
    }

}
