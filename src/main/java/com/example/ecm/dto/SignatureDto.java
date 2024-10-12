package com.example.ecm.dto;

import com.example.ecm.model.User;
import lombok.Data;

@Data
public class SignatureDto {
    private Long id;
    private String hash;
    private String placeholderTitle;
    private User user;
}
