package com.example.ecm.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignaturesSummary {
    private Long userId;
    private Integer signatureRequestsCount;
    private Map<Long, String> signatureRequestId2status;
}
