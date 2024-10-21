package com.example.ecm.mapper;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.model.Voting;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VotingMapper {

    public Voting toVoting(StartVotingRequest request, DocumentVersion documentVersion, List<SignatureRequest> signatureRequests, String status) {
        Voting voting = new Voting();
        voting.setDocumentVersion(documentVersion);
        voting.setApprovalThreshold(request.getApprovalThreshold());
        voting.setDeadline(request.getDeadline());
        voting.setCreatedAt(LocalDateTime.now());
        voting.setSignatureRequests(signatureRequests);
        voting.setStatus(status);
        return voting;
    }
}