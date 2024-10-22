package com.example.ecm.dto.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StartVotingResponse {

    private List<CreateUserResponse> participants;

    private CreateDocumentVersionResponse documentVersion;

    private Float approvalThreshold;

    private LocalDateTime deadline;

    private String status;
}