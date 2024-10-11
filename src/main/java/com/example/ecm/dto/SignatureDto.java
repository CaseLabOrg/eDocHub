package com.example.ecm.dto;

import com.example.ecm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignatureDto {
    private String hash;
    private String placeholderTitle;
    private User user;
}
