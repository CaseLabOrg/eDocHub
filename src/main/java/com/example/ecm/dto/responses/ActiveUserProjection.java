package com.example.ecm.dto.responses;

import com.example.ecm.model.User;

public interface ActiveUserProjection {
    User getUser();
    Long getDocumentsCreated();
}
