package com.example.ecm.dto.responses;

public interface DocumentSignatureRequestStatistics {
    Long getDocumentId();
    Long getRequestCount();
    Long getApprovedCount();
    Long getRejectedCount();
}
