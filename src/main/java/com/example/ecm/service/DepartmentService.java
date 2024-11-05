package com.example.ecm.service;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Department;
import com.example.ecm.model.User;
import com.example.ecm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentService {
    private final UserRepository userRepository;

    public Boolean checkPermission(Long documentId, Long userId) {
        User user = userRepository.findById(userId)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " not found"));

        return checkDocument(documentId, user.getDepartment());
    }

    private Boolean checkDocument(Long documentId, Department root) {
        if (root == null) {
            return false;
        }

        if (root.getDocuments().stream().anyMatch(doc -> doc.getId().equals(documentId))) {
            return true;
        }

        return root.getChildren().stream()
                .anyMatch(department -> checkDocument(documentId, department));
    }
}
