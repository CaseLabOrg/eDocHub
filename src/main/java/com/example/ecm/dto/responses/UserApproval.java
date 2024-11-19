package com.example.ecm.dto.responses;

public interface UserApproval {
    Long getUserId();
    Long getApprovalCount();
    String getDocumentTitle();
    Long getDocumentVersionId();
    String getApprovalType();
}
