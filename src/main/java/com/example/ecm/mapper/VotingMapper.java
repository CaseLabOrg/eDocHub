package com.example.ecm.mapper;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.CancelVotingResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.Voting;
import com.example.ecm.model.enums.VotingState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VotingMapper {
    private final UserMapper userMapper;
    private final DocumentVersionMapper documentVersionMapper;

    public Voting toVoting(StartVotingRequest request, DocumentVersion documentVersion, VotingState status) {
        Voting voting = new Voting();
        voting.setDocumentVersion(documentVersion);
        voting.setApprovalThreshold(request.getApprovalThreshold());
        voting.setDeadline(request.getDeadline());
        voting.setCreatedAt(LocalDate.now());
        voting.setStatus(status);
        return voting;
    }

    public StartVotingResponse toStartVotingResponse(Voting voting, String base64Content) {
        StartVotingResponse response = new StartVotingResponse();
        response.setParticipants(voting.getSignatureRequests().stream().map(r -> userMapper.toCreateUserResponse(r.getUserTo())).toList());
        CreateDocumentVersionResponse documentVersionResponse = documentVersionMapper.toCreateDocumentVersionResponse(voting.getDocumentVersion());
        documentVersionResponse.setBase64Content(base64Content);
        response.setDocumentVersion(documentVersionResponse);
        response.setDocumentId(voting.getDocumentVersion().getDocument().getId());
        response.setDeadline(voting.getDeadline());
        response.setApprovalThreshold(voting.getApprovalThreshold());
        response.setStatus(voting.getStatus());

        return response;
    }

    public CancelVotingResponse toCancelVotingResponse(Voting voting) {
        CancelVotingResponse response = new CancelVotingResponse();
        response.setId(voting.getId());
        response.setStatus(voting.getStatus().toString());
        return response;
    }
}