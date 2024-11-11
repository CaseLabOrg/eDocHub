package com.example.ecm.service;

import com.example.ecm.model.enums.DocumentState;

import java.util.List;
import java.util.Map;

public class DocumentStateService {
    private final Map<DocumentState, List<DocumentState>> transitionMap = Map.of(
            DocumentState.CREATED, List.of(DocumentState.SIGNED_BY_AUTHOR, DocumentState.MODIFIED, DocumentState.DELETED),
            DocumentState.DELETED, List.of(DocumentState.CREATED),
            DocumentState.SIGNED_BY_AUTHOR, List.of(DocumentState.SENT_ON_VOTING, DocumentState.SENT_ON_SIGNING, DocumentState.MODIFIED, DocumentState.DELETED)

    );
}
