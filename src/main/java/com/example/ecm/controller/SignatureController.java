package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateSignatureRequest;
import com.example.ecm.dto.requests.CreateSignatureRequestRequest;
import com.example.ecm.dto.requests.StartVotingRequest;
import com.example.ecm.dto.responses.CancelVotingResponse;
import com.example.ecm.dto.responses.CreateSignatureRequestResponse;
import com.example.ecm.dto.responses.GetSignatureResponse;
import com.example.ecm.dto.responses.StartVotingResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.SignatureService;
import com.example.ecm.service.VotingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

/**
 * Контроллер для работы с подписями в системе ECM.
 * Предоставляет конечные точки для создания, получения и управления запросами на подпись.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
@Loggable
public class SignatureController {

    private final VotingService votingService;
    private final SignatureService signatureService;

    /**
     * Получает запрос на подпись по идентификатору.
     *
     * @param id Идентификатор запроса на подпись.
     * @return Ответ с данными запроса на подпись.
     */
    @Operation(summary = "Получить запрос на подпись по ID", description = "Возвращает запрос на подпись по заданному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на подпись найден"),
            @ApiResponse(responseCode = "404", description = "Запрос на подпись не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreateSignatureRequestResponse> getSignatureRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(signatureService.getSignatureRequestById(id));
    }

    /**
     * Получает все существующие запросы на подпись.
     *
     * @return Список всех запросов на подпись.
     */
    @Operation(summary = "Получить все запросы на подпись", description = "Возвращает список всех запросов на подпись.")
    @ApiResponse(responseCode = "200", description = "Список запросов на подпись успешно получен")
    @GetMapping
    public ResponseEntity<List<CreateSignatureRequestResponse>> getAllSignatureRequests(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(defaultValue = "true") Boolean showAll) {
        return ResponseEntity.ok(signatureService.getAllSignatureRequests(userPrincipal, showAll));
    }

    /**
     * Отправляет запрос на подпись.
     *
     * @param signatureRequest Объект запроса с данными для подписи.
     * @param userPrincipal   Учетные данные текущего пользователя.
     * @return Ответ с данными созданного запроса на подпись.
     */
    @Operation(summary = "Отправить запрос на подпись", description = "Отправляет запрос на подпись на основе предоставленных данных.")
    @ApiResponse(responseCode = "200", description = "Запрос на подпись успешно отправлен")
    @PostMapping("/send")
    public ResponseEntity<CreateSignatureRequestResponse> sendToSign(@Valid @RequestBody CreateSignatureRequestRequest signatureRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(signatureService.sendToSign(signatureRequest, userPrincipal));
    }

    /**
     * Подписывает документ по заданному идентификатору.
     *
     * @param id Идентификатор запроса на подпись.
     * @param request Объект запроса с данными для подписания.
     * @param userPrincipal Учетные данные текущего пользователя.
     * @return Ответ с данными о подписанном запросе.
     */
    @Operation(summary = "Подписать документ", description = "Подписывает документ на основе предоставленных данных.")
    @ApiResponse(responseCode = "200", description = "Документ успешно подписан")
    @PostMapping("/{id}")
    public ResponseEntity<GetSignatureResponse> sendToSign(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "true") Boolean signByRequest,
                                                           @Valid @RequestBody CreateSignatureRequest request,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(signatureService.sign(id, request, signByRequest, userPrincipal));
    }

    /**
     * Начинает голосование для запроса на подпись.
     *
     * @param request Объект запроса с данными для начала голосования.
     * @return Ответ с данными о начале голосования.
     */
    @Operation(summary = "Начать голосование", description = "Начинает процесс голосования для запроса на подпись.")
    @ApiResponse(responseCode = "200", description = "Голосование успешно начато")
    @PostMapping("/voting")
    public ResponseEntity<StartVotingResponse> startVoting(
            @Valid @RequestBody StartVotingRequest request) {
        return ResponseEntity.ok(votingService.startVoting(request));
    }


    @GetMapping("/voting")
    public ResponseEntity<List<StartVotingResponse>> getVotings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(votingService.getVotings(userPrincipal));
    }

    /**
     * Отменяет голосование по идентификатору голосования.
     *
     * @param votingId Идентификатор голосования.
     * @return Ответ с данными об отмене голосования.
     */
    @Operation(summary = "Отменить голосование", description = "Отменяет голосование по заданному идентификатору.")
    @ApiResponse(responseCode = "200", description = "Голосование успешно отменено")
    @PutMapping("/voting/{votingId}/cancel")
    public ResponseEntity<CancelVotingResponse> cancelVoting(@PathVariable Long votingId) {
        return ResponseEntity.ok(votingService.cancelVoting(votingId));
    }

    /**
     * Делегировать подпись другому пользователю
     * @param signatureRequestId идентификатор подписи, для которой делегируется ответственность
     * @param id идентификатор пользователя, которому делегируется подпись
     * @param currentUser текущий пользователь, выполняющий делегирование
     */
    @PostMapping("/{signatureRequestId}/delegate/{id}")
    public ResponseEntity<Void> delegateSignature(@PathVariable Long signatureRequestId,
                                                  @PathVariable Long id,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        signatureService.delegateSignature(signatureRequestId, id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
