package com.example.ecm.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApproval {
    private Long userId;
    private Long approvalCount;
    private String documentTitle;
    private Long documentVersionId;
    private String approvalType;
}
