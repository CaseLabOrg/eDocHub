package com.example.ecm.mapper;

import com.example.ecm.dto.CreateSignatureRequest;
import com.example.ecm.model.Signature;
import org.springframework.stereotype.Component;

/**
 * Класс-мэппер для преобразования между сущностью {@link Signature} и DTO {@link CreateSignatureRequest}.
 * <p>
 * Мэппер предназначен для отделения слоя данных от слоя представления,
 * обеспечивая безопасную передачу данных между клиентом и сервером.
 */
@Component
public class SignatureMapper {

    /**
     * Преобразует объект {@link CreateSignatureRequest} в сущность {@link Signature}.
     *
     * @param dto объект {@link CreateSignatureRequest}, содержащий данные подписи
     * @return объект {@link Signature}, готовый для сохранения в базе данных
     */
    public Signature toSignature(CreateSignatureRequest dto) {
        Signature signature = new Signature();
        signature.setHash(dto.getHash());
        signature.setPlaceholderTitle(dto.getPlaceholderTitle());
        return signature;
    }

    /**
     * Преобразует сущность {@link Signature} в DTO {@link CreateSignatureRequest}.
     *
     * @param model объект {@link Signature}, представляющий данные подписи из базы данных
     * @return объект {@link CreateSignatureRequest}, содержащий данные для передачи клиенту
     */
    public CreateSignatureRequest fromSignature(Signature model) {
        CreateSignatureRequest createSignatureRequest = new CreateSignatureRequest();
        createSignatureRequest.setId(model.getId());
        createSignatureRequest.setHash(model.getHash());
        createSignatureRequest.setPlaceholderTitle(model.getPlaceholderTitle());
        return createSignatureRequest;
    }
}
