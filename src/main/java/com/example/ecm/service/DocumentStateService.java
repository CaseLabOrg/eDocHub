package com.example.ecm.service;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Document;
import com.example.ecm.model.enums.DocumentState;
import com.example.ecm.repository.DocumentRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentStateService {
    private final DocumentRepository documentRepository;
    private final Map<DocumentState, List<DocumentState>> transitionMap = Map.of(
            DocumentState.CREATED, List.of(DocumentState.SIGNED_BY_AUTHOR, DocumentState.MODIFIED, DocumentState.DELETED),
            DocumentState.DELETED, List.of(DocumentState.CREATED),
            DocumentState.SIGNED_BY_AUTHOR, List.of(DocumentState.SENT_ON_VOTING, DocumentState.SENT_ON_SIGNING, DocumentState.MODIFIED, DocumentState.DELETED),
            DocumentState.MODIFIED, List.of(DocumentState.SIGNED_BY_AUTHOR, DocumentState.DELETED, DocumentState.MODIFIED),
            DocumentState.SENT_ON_SIGNING, List.of(DocumentState.DELETED, DocumentState.MODIFIED, DocumentState.SENT_ON_REWORK, DocumentState.SIGNED),
            DocumentState.SENT_ON_REWORK, List.of(DocumentState.DELETED, DocumentState.MODIFIED),
            DocumentState.SENT_ON_VOTING, List.of(DocumentState.APPROVED_BY_VOTING, DocumentState.REJECTED_BY_VOTING),
            DocumentState.SIGNED, List.of(),
            DocumentState.APPROVED_BY_VOTING, List.of(),
            DocumentState.REJECTED_BY_VOTING, List.of()
    );

    public List<DocumentState> getTransitionsByDocumentId(Long id, Boolean showOnlyAlive,  UserPrincipal userPrincipal) {
        Optional<Document> document = documentRepository.findById(id);

        if (showOnlyAlive) {
            document = document.filter(Document::getIsAlive);
        }

        if (!userPrincipal.isAdmin()) {
            document = document.filter(d -> d.getUser().getId().equals(userPrincipal.getId()));
        }

        Document doc = document.orElseThrow(() -> new NotFoundException("Document with id: " + id + " not found"));
        return transitionMap.get(doc.getState());
    }

    public Boolean checkTransition(Document document, DocumentState newState) {
        return transitionMap.get(document.getState()).contains(newState);
    }
}
