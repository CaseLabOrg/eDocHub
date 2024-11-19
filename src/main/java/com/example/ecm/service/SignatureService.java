package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateSignatureRequest;
import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.dto.responses.GetSignatureResponse;
import com.example.ecm.exception.ConflictException;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.DocumentSignedEvent;
import com.example.ecm.kafka.service.EventProducerService;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.*;
import com.example.ecm.model.enums.DocumentState;
import com.example.ecm.model.enums.SignatureRequestState;
import com.example.ecm.repository.*;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final DocumentRepository documentRepository;
    private final SignatureRepository signatureRepository;
    private final UserRepository userRepository;
    private final SignatureMapper signatureMapper;
    private final EventProducerService eventProducerService;
    private final MailNotificationService mailNotificationService;
    private final DocumentStateService documentStateService;

    public CreateSignatureRequestResponse sendToSign(CreateSignatureRequestRequest request, UserPrincipal currentUser) {
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new NotFoundException("Document with id: " + request.getDocumentId() +" not found"));

        DocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(v -> v.getVersionId().equals(request.getDocumentVersionId()))
                .findFirst().orElseThrow(() -> new NotFoundException("Document version with id: " + request.getDocumentVersionId() +" not found"));

        User user = userRepository.findById(request.getUserIdTo()).orElseThrow(() -> new NotFoundException("User with id: " + request.getUserIdTo() + " not found"));

        if (!currentUser.getId().equals(document.getUser().getId()) && !currentUser.isAdmin()) {
            throw new ForbiddenException("You have no permission to send this document");
        }

        if (!documentStateService.checkTransition(document, DocumentState.SENT_ON_SIGNING)) {
            throw new ConflictException("You cannot send on signing document with id: " + request.getDocumentId() + " check available transitions");
        }

        mailNotificationService.notifyUserSignature(request.getUserIdTo(), documentVersion.getTitle());

        SignatureRequest signatureRequest = new SignatureRequest();
        signatureRequest.setUserTo(user);
        signatureRequest.setDocumentVersion(documentVersion);
        signatureRequest.setStatus(SignatureRequestState.PENDING);

        signatureRequest = signatureRequestRepository.save(signatureRequest);

        document.setState(DocumentState.SENT_ON_SIGNING);

        documentRepository.save(document);


        return signatureMapper.toCreateSignatureRequestResponse(signatureRequest);
    }

    public GetSignatureResponse sign(Long id, CreateSignatureRequest request, Boolean signByRequest, UserPrincipal currentUser) {

        if (!signByRequest) {
            Document document = documentRepository.findById(id)
                    .filter(Document::getIsAlive)
                    .filter(d -> d.getUser().getId().equals(currentUser.getId()))
                    .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));


            Signature signature = new Signature();
            signature.setUser(document.getUser());
            signature.setPlaceholderTitle(request.getPlaceholderTitle());
            signature.setDocumentVersion(document.getDocumentVersions().getLast());
            signature.setHash(document.getUser().hashCode());

            if (!documentStateService.checkTransition(document, DocumentState.SIGNED_BY_AUTHOR)) {
                throw new ConflictException("You cannot sign as author document with id: " + document.getId() + " check available transitions");
            }

            signature = signatureRepository.save(signature);
            document.setState(DocumentState.SIGNED_BY_AUTHOR);
            documentRepository.save(document);

            return signatureMapper.toGetSignatureResponse(signature);
        }

        List<SignatureRequest> requests = signatureRequestRepository.findAllByUserToId(currentUser.getId());
        if (requests.isEmpty()) {
            throw new NotFoundException("You have nothing to sign");
        }

        SignatureRequest signRequest = requests.stream()
                .filter(r -> r.getId().equals(id) && r.getStatus().equals(SignatureRequestState.PENDING))
                .findFirst().orElseThrow(() -> new NotFoundException("SignatureRequest with id: " + id + " not found or no longer active"));


        if (!documentStateService.checkTransition(signRequest.getDocumentVersion().getDocument(), DocumentState.SIGNED)) {
            throw new ConflictException("You cannot sign document with id: " + signRequest.getDocumentVersion().getDocument().getId() + " check available transitions");
        }
        signRequest.setStatus(request.getStatus());

        switch (request.getStatus()) {
            case APPROVED -> signRequest.getDocumentVersion().getDocument().setState(DocumentState.SIGNED);
            case REJECTED -> signRequest.getDocumentVersion().getDocument().setState(DocumentState.SENT_ON_REWORK);
        }

        documentRepository.save(signRequest.getDocumentVersion().getDocument());
        Signature signature = new Signature();
        signature.setUser(signRequest.getUserTo());
        signature.setPlaceholderTitle(request.getPlaceholderTitle());
        signature.setDocumentVersion(signRequest.getDocumentVersion());
        signature.setHash(signRequest.getUserTo().hashCode());

        DocumentSignedEvent event = new DocumentSignedEvent(id, currentUser.getId(), signRequest.getUserTo().getId(), request.getPlaceholderTitle());
        eventProducerService.sendDocumentSignedEvent(event);

        signature = signatureRepository.save(signature);
        return signatureMapper.toGetSignatureResponse(signature);
    }

    public List<CreateSignatureRequestResponse> getAllSignatureRequests(UserPrincipal userPrincipal, Boolean showAll) {
        return signatureRequestRepository.findAll().stream()
                .filter(signatureRequest -> signatureRequest.getDocumentVersion().getDocument().getUser().getId().equals(userPrincipal.getId()) || signatureRequest.getUserTo().getId().equals(userPrincipal.getId()))
                .map(signatureMapper::toCreateSignatureRequestResponse)
                .toList();
    }

    public CreateSignatureRequestResponse getSignatureRequestById(Long id, UserPrincipal userPrincipal) {
        return signatureRequestRepository.findById(id)
                .map(signatureMapper::toCreateSignatureRequestResponse)
                .orElseThrow(() -> new NotFoundException("SignatureRequest with id: " + id + " not found"));

    }
}
