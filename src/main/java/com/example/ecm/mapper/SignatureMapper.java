package com.example.ecm.mapper;

import com.example.ecm.dto.SignatureDto;
import com.example.ecm.model.Signature;
import org.springframework.stereotype.Component;

/**
 * Класс-мэппер для преобразования между сущностью {@link Signature} и DTO {@link SignatureDto}.
 * <p>
 * Мэппер предназначен для отделения слоя данных от слоя представления,
 * обеспечивая безопасную передачу данных между клиентом и сервером.
 */
@Component
public class SignatureMapper {

    /**
     * Преобразует объект {@link SignatureDto} в сущность {@link Signature}.
     *
     * @param dto объект {@link SignatureDto}, содержащий данные подписи
     * @return объект {@link Signature}, готовый для сохранения в базе данных
     */
    public Signature toSignature(SignatureDto dto) {
        Signature signature = new Signature();
        signature.setHash(dto.getHash());
        signature.setPlaceholderTitle(dto.getPlaceholderTitle());
        signature.setUser(dto.getUser());
        return signature;
    }

    /**
     * Преобразует сущность {@link Signature} в DTO {@link SignatureDto}.
     *
     * @param model объект {@link Signature}, представляющий данные подписи из базы данных
     * @return объект {@link SignatureDto}, содержащий данные для передачи клиенту
     */
    public SignatureDto fromSignature(Signature model) {
        SignatureDto signatureDto = new SignatureDto();
        signatureDto.setId(model.getId());
        signatureDto.setHash(model.getHash());
        signatureDto.setPlaceholderTitle(model.getPlaceholderTitle());
        signatureDto.setUser(model.getUser());
        return signatureDto;
    }
}
