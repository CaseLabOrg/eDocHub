package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.VotingMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.model.User;
import com.example.ecm.model.Voting;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.repository.DocumentVersionRepository;
import com.example.ecm.repository.SignatureRequestRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.repository.VotingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final VotingRepository votingRepository;
    private final VotingMapper votingMapper;
    private final DocumentVersionRepository documentServiceRepository;
    @Qualifier("votingFinisherScheduler")
    private final ScheduledExecutorService votingFinisherScheduler;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SignatureRequestRepository signatureRequestRepository;

    public StartVotingResponse startVoting(StartVotingRequest startVotingRequest) {
        DocumentVersion documentVersion = documentServiceRepository.findById(startVotingRequest.getDocumentVersionId())
                .orElseThrow(() -> new NotFoundException("Document version with id: " + startVotingRequest.getDocumentVersionId() +" not found"));

        List<SignatureRequest> signatureRequests = sendAllParticipantsToVote(startVotingRequest);
        Voting voting = votingMapper.toVoting(startVotingRequest, documentVersion, "ACTIVE");
        signatureRequests.forEach(r -> r.setVoting(voting));
        voting.setSignatureRequests(signatureRequests);
        votingRepository.save(voting);

        votingFinisherScheduler.schedule(() -> completeVoting(voting.getId()),
                Duration.between(LocalDateTime.now(), voting.getDeadline()).getSeconds(), TimeUnit.SECONDS);

        return votingMapper.toStartVotingResponse(voting);
    }

    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = TimeUnit.DAYS)
    private void activeVotingsResultUpdate() {
        votingRepository.findByStatus("ACTIVE").forEach(voting -> {
            int all = voting.getSignatureRequests().size();
            long inFavor = voting.getSignatureRequests().stream()
                    .filter(signatureRequest -> signatureRequest.getStatus().equals("FOR"))
                    .count();
            voting.setCurrentApprovalRate(all / (float) inFavor);
            votingRepository.save(voting);
        });
    }

    private void completeVoting(Long votingId) {
        Voting voting = votingRepository.findById(votingId)
                .orElseThrow(() -> new NotFoundException("Voting with id: " + votingId + " not found"));
        voting.setStatus("COMPLETED");
        votingRepository.save(voting);
    }

    private SignatureRequest sendToVote(Long id, CreateSignatureRequestRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document with id: " + id +" not found"));

        DocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(v -> v.getVersionId().equals(request.getDocumentVersionId()))
                .findFirst().orElseThrow(() -> new NotFoundException("Document version with id: " + id +" not found"));

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));

        SignatureRequest signatureRequest = new SignatureRequest();
        signatureRequest.setUserTo(user);
        signatureRequest.setDocumentVersion(documentVersion);
        signatureRequest.setStatus("PENDING");

        return signatureRequestRepository.save(signatureRequest);
    }

    private List<SignatureRequest> sendAllParticipantsToVote(StartVotingRequest startVotingRequest) {
        long documentId = startVotingRequest.getDocumentId();
        long documentVersionId = startVotingRequest.getDocumentVersionId();

        List<SignatureRequest> signatureRequests = new ArrayList<>();
        for (long participantId : startVotingRequest.getParticipantIds()) {
            CreateSignatureRequestRequest createSignatureRequestRequest = new CreateSignatureRequestRequest();
            createSignatureRequestRequest.setDocumentVersionId(documentVersionId);
            createSignatureRequestRequest.setUserIdTo(participantId);

            signatureRequests.add(sendToVote(documentId, createSignatureRequestRequest));
        }
        return signatureRequests;
    }
}
