package com.example.ecm.mapper;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.Voting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VotingMapper {
    private final UserMapper userMapper;
    private final DocumentVersionMapper documentVersionMapper;

    public Voting toVoting(StartVotingRequest request, DocumentVersion documentVersion, String status) {
        Voting voting = new Voting();
        voting.setDocumentVersion(documentVersion);
        voting.setApprovalThreshold(request.getApprovalThreshold());
        voting.setDeadline(request.getDeadline());
        voting.setCreatedAt(LocalDateTime.now());
        voting.setStatus(status);
        return voting;
    }

    public StartVotingResponse toStartVotingResponse(Voting voting) {
        StartVotingResponse response = new StartVotingResponse();
        response.setParticipants(voting.getSignatureRequests().stream().map(r -> userMapper.toCreateUserResponse(r.getUserTo())).toList());
        response.setDocumentVersion(documentVersionMapper.toCreateDocumentVersionResponse(voting.getDocumentVersion()));
        response.setDeadline(voting.getDeadline());
        response.setApprovalThreshold(voting.getApprovalThreshold());
        response.setStatus(voting.getStatus());

        return response;
    }
}