package com.example.ecm.dto.responses;

public interface DailyApprovalStats {
    int getYear();
    int getMonth();
    int getDay();
    long getApprovalsCount();
    long getDailyGrowth();
    long getCumulativeCount();
}
