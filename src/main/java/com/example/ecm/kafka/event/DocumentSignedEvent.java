package com.example.ecm.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DocumentSignedEvent {
    private Long documentVersionId;
    private Long userId;
    private Long signerId;
    private String placeholderTitle;
}
