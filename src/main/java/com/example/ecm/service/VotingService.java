package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.CancelVotingResponse;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.exception.ConflictException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.VotingMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.model.User;
import com.example.ecm.model.Voting;
import com.example.ecm.model.enums.DocumentState;
import com.example.ecm.model.enums.SignatureRequestState;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.DocumentVersionRepository;
import com.example.ecm.repository.SignatureRequestRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.repository.VotingRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final VotingRepository votingRepository;
    private final VotingMapper votingMapper;
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SignatureRequestRepository signatureRequestRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final MailNotificationService mailNotificationService;
    private final DocumentStateService documentStateService;
    private final MinioService minioService;


    public List<StartVotingResponse> getVotings(UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getId();
        List<Voting> votings = votingRepository.findAll().stream()
                .filter(v -> v.getSignatureRequests().stream()
                        .map(SignatureRequest::getUserTo)
                        .anyMatch(user -> user.getId().equals(userId))
                ).toList();
        List<String> contents = votings.stream().map(v -> minioService.getBase64DocumentByName(v.getDocumentVersion().getId() + "_" + v.getDocumentVersion().getTitle())).toList();

        return IntStream.range(0, Math.min(votings.size(), contents.size()))
                .mapToObj(i -> votingMapper.toStartVotingResponse(votings.get(i), contents.get(i))).toList();
    }

    public StartVotingResponse startVoting(StartVotingRequest startVotingRequest) {
        DocumentVersion documentVersion = documentVersionRepository.findByDocumentIdAndVersionId(startVotingRequest.getDocumentId(), startVotingRequest.getDocumentVersionId())
                .orElseThrow(() -> new NotFoundException("Document Version with id: " + startVotingRequest.getDocumentId() + " or Document id " + startVotingRequest.getDocumentVersionId() + " not found"));
        String base64Content = minioService.getBase64DocumentByName(documentVersion.getId() + "_" + documentVersion.getTitle());

        if (!documentStateService.checkTransition(documentVersion.getDocument(), DocumentState.SENT_ON_VOTING)) {
            throw new ConflictException("You cannot send on voting document with id: " + documentVersion.getDocument().getId() + " check available transitions");
        }


        List<SignatureRequest> signatureRequests = sendAllParticipantsToVote(startVotingRequest);
        Voting voting = votingMapper.toVoting(startVotingRequest, documentVersion, "ACTIVE");

        voting.getDocumentVersion().getDocument().setState(DocumentState.SENT_ON_VOTING);

        signatureRequests.forEach(r -> r.setVoting(voting));
        voting.setSignatureRequests(signatureRequests);
        votingRepository.save(voting);

        return votingMapper.toStartVotingResponse(voting, base64Content);
    }

    public CancelVotingResponse cancelVoting(Long votingId) {
        Voting voting = votingRepository.findById(votingId)
                .orElseThrow(() -> new NotFoundException("Voting with id: " + votingId + " not found"));

        if (!voting.getStatus().equals("ACTIVE")) {
            throw new NotFoundException("Voting with id: " + votingId + " is not active");
        }
        voting.setStatus("NEW_CANCELED");
        votingRepository.save(voting);

        return votingMapper.toCancelVotingResponse(voting);
    }

    @Scheduled(cron = "@daily")
    private void votingsResultsUpdate() {
        votingRepository.findByStatus("ACTIVE").forEach(voting -> {
            int all = voting.getSignatureRequests().size();
            long inFavor = voting.getSignatureRequests().stream()
                    .filter(signatureRequest -> signatureRequest.getStatus().equals(SignatureRequestState.APPROVED))
                    .count();
            voting.setCurrentApprovalRate(all / (float) inFavor);

            if (LocalDateTime.now().isAfter(voting.getDeadline().atStartOfDay())) {
                voting.setStatus("COMPLETED");
                if (voting.getCurrentApprovalRate() >= voting.getApprovalThreshold()) {
                    voting.getDocumentVersion().getDocument().setState(DocumentState.APPROVED_BY_VOTING);
                } else {
                    voting.getDocumentVersion().getDocument().setState(DocumentState.REJECTED_BY_VOTING);
                }
                documentRepository.save(voting.getDocumentVersion().getDocument());
                notifyParticipants(voting);
            }

            votingRepository.save(voting);
        });
        votingRepository.findByStatus("NEW_CANCELED").forEach(voting -> {
            voting.setStatus("CANCELED");
            notifyParticipants(voting);
        });
    }

    private void notifyParticipants(Voting voting) {
        for (SignatureRequest signatureRequest : voting.getSignatureRequests()) {
            String text;
            String documentTitle = voting.getDocumentVersion().getTitle();
            if (voting.getStatus().equals("CANCELED"))  {
                text = "Голосование по принятию документа \"%s\" было отменено".formatted(documentTitle);
            } else  {
                text = "Голосование по принятию документа \"%s\"завершилось. Благодарим за участие! Поддержало: %s%%, необходимо для принятия: %s%%."
                    .formatted(documentTitle, voting.getCurrentApprovalRate(), voting.getApprovalThreshold());
            }

            mailNotificationService.send(signatureRequest.getUserTo().getEmail(), "Результаты голосования", text);
        }
    }

    private SignatureRequest sendToVote(CreateSignatureRequestRequest request) {
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new NotFoundException("Document with id: " + request.getDocumentId() +" not found"));

        DocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(v -> v.getVersionId().equals(request.getDocumentVersionId()))
                .findFirst().orElseThrow(() -> new NotFoundException("Document version with id: " + request.getDocumentVersionId() +" not found"));

        User user = userRepository.findById(request.getUserIdTo()).orElseThrow(() -> new NotFoundException("User with id: " + request.getUserIdTo() + " not found"));

        SignatureRequest signatureRequest = new SignatureRequest();
        signatureRequest.setUserTo(user);
        signatureRequest.setDocumentVersion(documentVersion);
        signatureRequest.setStatus(SignatureRequestState.PENDING);

        return signatureRequestRepository.save(signatureRequest);
    }

    private List<SignatureRequest> sendAllParticipantsToVote(StartVotingRequest startVotingRequest) {
        long documentVersionId = startVotingRequest.getDocumentVersionId();

        List<SignatureRequest> signatureRequests = new ArrayList<>();
        for (long participantId : startVotingRequest.getParticipantIds()) {
            CreateSignatureRequestRequest createSignatureRequestRequest = new CreateSignatureRequestRequest();
            createSignatureRequestRequest.setDocumentId(startVotingRequest.getDocumentId());
            createSignatureRequestRequest.setDocumentVersionId(documentVersionId);
            createSignatureRequestRequest.setUserIdTo(participantId);

            signatureRequests.add(sendToVote(createSignatureRequestRequest));
        }
        return signatureRequests;
    }
}
