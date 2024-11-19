package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateSignatureRequest;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.dto.responses.GetSignatureResponse;
import com.example.ecm.model.Document;
import com.example.ecm.model.DocumentVersion;
import com.example.ecm.model.Signature;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.repository.DocumentVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Класс-мэппер для преобразования между сущностью {@link Signature} и DTO {@link CreateSignatureRequest}.
 * <p>
 * Мэппер предназначен для отделения слоя данных от слоя представления,
 * обеспечивая безопасную передачу данных между клиентом и сервером.
 */
@Component
@RequiredArgsConstructor
public class SignatureMapper {
    private final UserMapper userMapper;

    /**
     * Преобразует объект {@link CreateSignatureRequest} в сущность {@link Signature}.
     *
     * @return объект {@link Signature}, готовый для сохранения в базе данных
     */
    public Signature toSignature(Document document, DocumentVersion documentVersion) {
        Signature signature = new Signature();
        signature.setHash(document.getUser().hashCode());
        signature.setDocumentVersion(documentVersion);
        signature.setUser(document.getUser());
        return signature;
    }

    /**
     * Преобразует сущность {@link Signature} в DTO {@link CreateSignatureRequest}.
     *
     * @param signature объект {@link Signature}, представляющий данные подписи из базы данных
     * @return объект {@link CreateSignatureRequest}, содержащий данные для передачи клиенту
     */
    public GetSignatureResponse toGetSignatureResponse(Signature signature) {
        GetSignatureResponse createSignatureRequest = new GetSignatureResponse();
        createSignatureRequest.setHash(signature.getHash());
        createSignatureRequest.setPlaceholderTitle(signature.getPlaceholderTitle());
        createSignatureRequest.setUser(userMapper.toCreateUserResponse(signature.getUser()));
        return createSignatureRequest;
    }

    public CreateSignatureRequestResponse toCreateSignatureRequestResponse(SignatureRequest signatureRequest) {
        CreateSignatureRequestResponse response = new CreateSignatureRequestResponse();
        response.setId(signatureRequest.getId());
        response.setUserTo(userMapper.toCreateUserResponse(signatureRequest.getUserTo()));
        response.setDocumentId(signatureRequest.getDocumentVersion().getDocument().getId());
        response.setDocumentVersionId(signatureRequest.getDocumentVersion().getId());
        return response;
    }
}
