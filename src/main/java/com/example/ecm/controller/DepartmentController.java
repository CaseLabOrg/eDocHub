package com.example.ecm.controller;

import com.example.ecm.dto.requests.CreateDepartmentRequest;
import com.example.ecm.dto.responses.CreateDepartmentResponse;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Создать новый департамент
     * @param request данные для создания департамента
     * @return созданный департамент
     */
    @PostMapping
    public ResponseEntity<CreateDepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    /**
     * Получить департамент по идентификатору
     * @param id идентификатор департамента
     * @return департамент
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateDepartmentResponse> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * Обновить департамент
     * @param id идентификатор департамента
     * @param request данные для обновления департамента
     * @param userPrincipal текущий пользователь
     * @return обновленный департамент
     */
    @PutMapping("/{id}")
    public ResponseEntity<CreateDepartmentResponse> updateDepartment(@PathVariable Long id,
                                                                     @Valid @RequestBody CreateDepartmentRequest request,
                                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request, userPrincipal));
    }

    /**
     * Удалить департамент (сделать его неактивным)
     * @param id идентификатор департамента
     * @param userPrincipal текущий пользователь
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        departmentService.deleteDepartmentById(id, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстановить департамент
     * @param id идентификатор департамента
     * @param userPrincipal текущий пользователь
     */
    @PutMapping("/{id}/recover")
    public ResponseEntity<Void> recoverDepartment(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        departmentService.recoverDepartmentById(id, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    /**
     * Добавить участников в департамент
     * @param id идентификатор департамента
     * @param membersId список идентификаторов участников
     * @param userPrincipal текущий пользователь
     * @return обновленный департамент
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<CreateDepartmentResponse> addMembers(@PathVariable Long id,
                                                               @RequestBody List<Long> membersId,
                                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(departmentService.addMembers(id, membersId, userPrincipal));
    }

    /**
     * Исключить участников из департамента
     * @param id идентификатор департамента
     * @param membersId список идентификаторов участников
     * @param userPrincipal текущий пользователь
     * @return обновленный департамент
     */
    @DeleteMapping("/{id}/members")
    public ResponseEntity<CreateDepartmentResponse> deleteMembers(@PathVariable Long id,
                                                                  @RequestBody List<Long> membersId,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(departmentService.deleteMembers(id, membersId, userPrincipal));
    }
}