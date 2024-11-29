package com.example.ecm.dto.responses;

import com.example.ecm.model.enums.VotingState;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StartVotingResponse {

    private List<CreateUserResponse> participants;

    private Long documentId;

    private CreateDocumentVersionResponse documentVersion;

    private Float approvalThreshold;

    private LocalDate deadline;

    private VotingState status;
}