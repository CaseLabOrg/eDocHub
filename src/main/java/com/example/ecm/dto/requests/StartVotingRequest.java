package com.example.ecm.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StartVotingRequest {

    @NotNull(message = "participantIds cannot be null")
    private List<Long> participantIds;

    @NotNull(message = "documentId cannot be null")
    private Long documentId;

    @NotNull(message = "documentVersionId cannot be null")
    private Long documentVersionId;

    @Min(value = 0, message = "approvalThreshold must be between 0 and 100")
    @Max(value = 100, message = "approvalThreshold must be between 0 and 100")
    @NotNull(message = "approvalThreshold cannot be null")
    private Float approvalThreshold;

    @Future(message = "deadline cannot be in the past")
    @NotNull(message = "approvalThreshold cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deadline;

}
