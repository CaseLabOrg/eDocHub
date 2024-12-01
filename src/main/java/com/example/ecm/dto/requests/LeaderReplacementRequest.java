package com.example.ecm.dto.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;

@Data
public class LeaderReplacementRequest {

    @NotNull(message = "successorId cannot be null")
    private Long successorId;

    @NotNull(message = "until cannot be null")
    @Future(message = "until date cannot be in the past")
    private LocalDate until;
}
