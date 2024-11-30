package com.example.ecm.dto.responses;

import lombok.ToString;

public interface DailyApprovalStats {
    int getYear();
    int getMonth();
    int getDay();
    long getApprovalsCount();
    long getDailyGrowth();
    long getCumulativeCount();
}
