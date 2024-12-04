package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateSignatureRequest;
import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.dto.responses.GetSignatureResponse;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.kafka.event.DocumentSignedEvent;
import com.example.ecm.kafka.service.DocumentEventProducer;
import com.example.ecm.mapper.SignatureMapper;
import com.example.ecm.model.*;
import com.example.ecm.repository.*;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final SignatureRepository signatureRepository;
    private final UserRepository userRepository;
    private final SignatureMapper signatureMapper;
    private final DocumentEventProducer documentEventProducer;
    private final MailNotificationService mailNotificationService;
    private final DepartmentService departmentService;
    private final UserReplacementsRepository userReplacementsRepository;

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

        Signature signature = signatureMapper.toSignature(document, documentVersion);

        documentVersion.getSignatures().add(signature);

        signatureRepository.save(signature);

        documentVersionRepository.save(documentVersion);

        mailNotificationService.notifyUserSignature(request.getUserIdTo(), documentVersion.getTitle());

        SignatureRequest signatureRequest = new SignatureRequest();
        signatureRequest.setUserTo(user);
        signatureRequest.setDocumentVersion(documentVersion);
        signatureRequest.setStatus("PENDING");

        signatureRequest = signatureRequestRepository.save(signatureRequest);


        return signatureMapper.toCreateSignatureRequestResponse(signatureRequest);
    }

    public GetSignatureResponse sign(Long id, CreateSignatureRequest request, UserPrincipal currentUser) {

        List<SignatureRequest> requests = getAllSignatureRequestsOfUser(currentUser.getId());

        requests.addAll(userReplacementsRepository.findAllCurrentlyReplacedByUser(currentUser.getId())
                .stream()
                .map(replacedUser -> getAllSignatureRequestsOfUser(replacedUser.getId()))
                .flatMap(Collection::stream)
                .toList()
        );
        if (requests.isEmpty()) {
            throw new NotFoundException("You have nothing to sign");
        }

        SignatureRequest signRequest = requests.stream()
                .filter(r -> r.getId().equals(id) && r.getStatus().equals("PENDING"))
                .findFirst().orElseThrow(() -> new NotFoundException("SignatureRequest with id: " + id + " not found or no longer active"));

        signRequest.setStatus(request.getStatus());

        Signature signature = new Signature();
        signature.setUser(signRequest.getUserTo());
        signature.setPlaceholderTitle(request.getPlaceholderTitle());
        signature.setDocumentVersion(signRequest.getDocumentVersion());
        signature.setHash(signRequest.getUserTo().hashCode());

        DocumentSignedEvent event = new DocumentSignedEvent(id, currentUser.getId(), signRequest.getUserTo().getId(), request.getPlaceholderTitle());
        documentEventProducer.sendDocumentSignedEvent(event);

        signature = signatureRepository.save(signature);

        return signatureMapper.toGetSignatureResponse(signature);
    }

    public List<CreateSignatureRequestResponse> getAllSignatureRequests() {
        return signatureRequestRepository.findAll()
                .stream().map(signatureMapper::toCreateSignatureRequestResponse).toList();
    }

    public CreateSignatureRequestResponse getSignatureRequestById(Long id) {
        return signatureRequestRepository.findById(id).map(signatureMapper::toCreateSignatureRequestResponse)
                .orElseThrow(() -> new NotFoundException("SignatureRequest with id: " + id + " not found"));

    }

    /**
     * Делегировать подпись
     * @param id идентификатор пользователя, которому делегируют
     * @param currentUser текущий пользователь
     */
    public void delegateSignature(Long signatureRequestId, Long id, UserPrincipal currentUser) {
        SignatureRequest signatureRequest = signatureRequestRepository.findById(signatureRequestId)
                .orElseThrow(() -> new NotFoundException("SignatureRequest with id: " + signatureRequestId + " not found"));

        User member = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        User leader = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + currentUser.getId() + " not found"));
        if(!departmentService.isMemberInAnyDepartmentOfLeader(member, leader))
            throw new ForbiddenException("User with id: " + member.getId() + " is not a member of any department of the leader with id: " + leader.getId());

        signatureRequest.setDelegatedTo(member);
        signatureRequestRepository.save(signatureRequest);
    }

    private List<SignatureRequest> getAllSignatureRequestsOfUser(Long userId) {
        List<SignatureRequest> requests = signatureRequestRepository.findAllByUserToId(userId);
        requests.addAll(signatureRequestRepository.findAllByDelegatedToId(userId));
        return requests;
    }
}
