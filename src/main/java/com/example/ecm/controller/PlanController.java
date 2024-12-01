package com.example.ecm.controller;

import com.example.ecm.dto.requests.CreatePlanRequest;
import com.example.ecm.dto.responses.CreatePlanResponse;
import com.example.ecm.service.PlanService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanService planService;

    @Operation(summary = "Создать новый план", description = "Создает новый план на основе предоставленных данных")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное создание плана"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<CreatePlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(planService.createPlan(request));
    }

    @Operation(summary = "Получить план по ID", description = "Возвращает данные плана по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "План найден"),
            @ApiResponse(responseCode = "404", description = "План не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreatePlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @Operation(summary = "Получить все планы", description = "Возвращает список всех планов")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка планов")
    @GetMapping
    public ResponseEntity<List<CreatePlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @Operation(summary = "Обновить план", description = "Обновляет план по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "План успешно обновлен"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав"),
            @ApiResponse(responseCode = "404", description = "План не найден")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CreatePlanResponse> updatePlan(@PathVariable Long id, @Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }

    @Operation(summary = "Удалить план", description = "Удаляет план по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "План успешно удален"),
            @ApiResponse(responseCode = "401", description = "У вас нет прав"),
            @ApiResponse(responseCode = "404", description = "План не найден")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}