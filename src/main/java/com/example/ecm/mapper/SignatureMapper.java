package com.example.ecm.mapper;

import com.example.ecm.dto.SignatureDto;
import com.example.ecm.model.Signature;
import org.springframework.stereotype.Component;

@Component
public class SignatureMapper {
    public Signature toSignature(SignatureDto dto) {
        return new Signature(dto.getPlaceholderTitle(), dto.getHash(), dto.getUser());
    }

    public SignatureDto fromSignature(Signature model) {
        return new SignatureDto(model.getHash(), model.getPlaceholderTitle(), model.getUser());
    }
}
