package com.example.ecm.mapper;

import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.CancelVotingResponse;
import com.example.ecm.dto.responses.CreateDocumentVersionResponse;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Attribute;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.Value;
import com.example.ecm.model.Voting;
import com.example.ecm.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
        response.setDeadline(voting.getDeadline());
        response.setApprovalThreshold(voting.getApprovalThreshold());
        response.setStatus(voting.getStatus());

        return response;
    }

    public CancelVotingResponse toCancelVotingResponse(Voting voting) {
        CancelVotingResponse response = new CancelVotingResponse();
        response.setId(voting.getId());
        response.setStatus(voting.getStatus());
        return response;
    }


    public Map<String, String> toElasticsearchValues(Map<Attribute, Value> values) {
        if (values == null) return null;
        Map<String, String> elasticValues = new HashMap<>();
        for (Map.Entry<Attribute, Value> entry : values.entrySet()) {
            Attribute attribute = entry.getKey();
            Value value = entry.getValue();
            if (attribute != null && value != null) {
                elasticValues.put(attribute.getName(), value.getValue()); // Assuming Attribute has getName and Value has getValue
            }
        }
        return elasticValues;
    }

}