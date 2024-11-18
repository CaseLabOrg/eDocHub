package com.example.ecm.controller;

import com.example.ecm.dto.responses.CreateSubscriptionResponse;
import com.example.ecm.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "Получить подписку по ID", description = "Возвращает данные подписки по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Подписка найдена"),
            @ApiResponse(responseCode = "404", description = "Подписка не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreateSubscriptionResponse> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @Operation(summary = "Получить все подписки", description = "Возвращает список всех подписок")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное получение списка подписок"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<CreateSubscriptionResponse>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(
            summary = "Pause subscription by id",
            tags = {"Subscriptions", "SUPER_ADMIN"},
            parameters = {
                    @Parameter(name = "id", description = "Subscription id", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription paused successful",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Does not have access rights"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/pause/{id}")
    public void pause(@PathVariable Long id) {
        subscriptionService.pauseSubscription(id);
    } //test

    @Operation(
            summary = "Activate subscription by id",
            tags = {"Subscriptions", "SUPER_ADMIN"},
            parameters = {
                    @Parameter(name = "id", description = "Subscription id", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription activated successful",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Does not have access rights"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/activate/{id}")
    public void activate(@PathVariable Long id) {
        subscriptionService.resumeSubscription(id);
    } //test
}
