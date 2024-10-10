package com.example.ecm.dto;



import com.example.ecm.model.DocumentType;
import com.example.ecm.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateDocumentRequest {

        private String title;

        private User user;

        private DocumentType documentType;

        private String description;

        private LocalDateTime created_at;

        private Integer version;

}