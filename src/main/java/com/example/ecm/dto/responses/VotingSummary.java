package com.example.ecm.dto.responses;

public interface VotingSummary {
    Long getDocumentVersionId();
    String getDocumentTitle();
    Long getParticipantCount();
    String getParticipants();
    String getVotingStatus();
    Float getApprovalThreshold();
    Float getCurrentApprovalRate();
}
