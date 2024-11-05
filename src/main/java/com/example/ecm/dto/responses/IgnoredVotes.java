package com.example.ecm.dto.responses;

import lombok.Data;

@Data
public class IgnoredVotes {
    private Long userId;
    private String userName;
    private Long ignoredVoteCount;
}

