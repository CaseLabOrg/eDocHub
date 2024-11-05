package com.example.ecm.dto.responses;

import lombok.Data;

@Data
public class OnlineVoteSummary {
    private Long voteCount;
    private String documentTitle;
    private String votingStatus;
    private Long participantCount;
}
