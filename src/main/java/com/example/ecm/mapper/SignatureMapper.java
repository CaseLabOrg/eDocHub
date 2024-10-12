package com.example.ecm.mapper;

import com.example.ecm.dto.SignatureDto;
import com.example.ecm.model.Signature;
import org.springframework.stereotype.Component;

@Component
public class SignatureMapper {
    public Signature toSignature(SignatureDto dto) {
        Signature signature = new Signature();
        signature.setHash(dto.getHash());
        signature.setPlaceholderTitle(dto.getPlaceholderTitle());
        signature.setUser(dto.getUser());
        return signature;
    }

    public SignatureDto fromSignature(Signature model) {
        SignatureDto signatureDto = new SignatureDto();
        signatureDto.setId(model.getId());
        signatureDto.setHash(model.getHash());
        signatureDto.setPlaceholderTitle(model.getPlaceholderTitle());
        signatureDto.setUser(model.getUser());
        return signatureDto;
    }
}
