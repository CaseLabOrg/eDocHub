package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.patch_requests.PatchUserRequest;
import com.example.ecm.dto.requests.CreateUserRequest;
import com.example.ecm.dto.requests.LeaderReplacementRequest;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.dto.requests.PutRoleRequest;
import com.example.ecm.security.UserPrincipal;
import com.example.ecm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 * Предоставляет конечные точки для создания, получения, обновления и удаления пользователей.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

    private final UserService userService;

    /**
     * Создает нового пользователя.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя.
     * @param userPrincipal     Информация о текущем аутентифицированном пользователе.
     * @return DTO с данными созданного пользователя.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя с указанными данными.")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно создан")
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CreateUserResponse userResponse = userService.createUser(createUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Создает администратора в указанной тенантной среде.
     *
     * @param createUserRequest DTO с данными для создания пользователя.
     * @param userPrincipal     Информация о текущем аутентифицированном пользователе.
     * @param tenantId          Идентификатор тенанта.
     * @return DTO с данными созданного администратора.
     */
    @PreAuthorize("hasAnyAuthority('OWNER')")
    @PostMapping("/createAdmin")
    @Operation(summary = "Создать администратора", description = "Создает нового администратора в указанной тенантной среде.")
    @ApiResponse(responseCode = "200", description = "Администратор успешно создан")
    public ResponseEntity<CreateUserResponse> createAdminUser(
            @Valid @RequestBody CreateUserRequest createUserRequest) {
        CreateUserResponse userResponse = userService.createUserAdmin(createUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Создает владельца в указанной тенантной среде.
     *
     * @param createUserRequest DTO с данными для создания пользователя.
     * @param tenantId          Идентификатор тенанта.
     * @return DTO с данными созданного владельца.
     */
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/createOwner")
    @Operation(summary = "Создать владельца", description = "Создает нового владельца в указанной тенантной среде.")
    @ApiResponse(responseCode = "200", description = "Владелец успешно создан")
    public ResponseEntity<CreateUserResponse> createOwner(
            @Valid @RequestBody CreateUserRequest createUserRequest,
            Long tenantId) {
        CreateUserResponse userResponse = userService.createUserOwner(createUserRequest, tenantId);
        return ResponseEntity.ok(userResponse);
    }


    /**
     * Получение пользователя по его ID с возвратом данных в виде DTO.
     *
     * @param id Идентификатор пользователя.
     * @param showOnlyAlive Параметр для фильтрации пользователей по статусу (по умолчанию true).
     * @return DTO с данными пользователя, если найден, или 404 Not Found.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @GetMapping("/{id}")
    public ResponseEntity<CreateUserResponse> getUserById(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean showOnlyAlive) {
        return ResponseEntity.ok(userService.getUserById(id, showOnlyAlive));
    }

    /**
     * Получение списка всех пользователей с возвратом данных в виде DTO.
     *
     * @param showOnlyAlive Параметр для фильтрации пользователей по статусу (по умолчанию true).
     * @return Список DTO с данными всех пользователей.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей.")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно возвращен")
    @GetMapping
    public ResponseEntity<List<CreateUserResponse>> getAllUsers(@RequestParam(defaultValue = "true") Boolean showOnlyAlive, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CreateUserResponse> users = userService.getAllUsers(showOnlyAlive, userPrincipal);
        return ResponseEntity.ok(users);
    }

    /**
     * Добавление роли пользователю.
     *
     * @param role DTO с данными роли для добавления.
     * @return DTO с обновленными данными пользователя.
     */
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Добавить роль пользователю", description = "Добавляет указанную роль пользователю по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Роль успешно добавлена")
    @PutMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> addRole(@PathVariable Long id, @Valid @RequestBody PutRoleRequest role) {
        return ResponseEntity.ok(userService.addRole(id, role));
    }

    /**
     * Удаление роли у пользователя.
     *
     * @param id Идентификатор пользователя.
     * @param role DTO с данными роли для удаления.
     * @return DTO с обновленными данными пользователя.
     */
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Удалить роль у пользователя", description = "Удаляет указанную роль у пользователя по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Роль успешно удалена")
    @DeleteMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> removeRole(@PathVariable Long id, @Valid @RequestBody PutRoleRequest role
                                                      ) {
        return ResponseEntity.ok(userService.removeRole(id, role));
    }

    /**
     * Обновление данных пользователя по его ID на основе DTO.
     *
     * @param id Идентификатор пользователя.
     * @param updateUserRequest DTO с обновленными данными пользователя.
     * @return Обновленный пользователь в виде DTO.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Обновить данные пользователя", description = "Обновляет данные пользователя по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены")
    @PutMapping("/{id}")
    public ResponseEntity<CreateUserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest updateUserRequest) {
        CreateUserResponse updatedUser = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя.
     * @return Ответ без содержимого (204 No Content), если пользователь был удален.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его идентификатору.")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстановление пользователя по его ID.
     *
     * @param id Идентификатор пользователя.
     * @return Ответ без содержимого (204 No Content), если пользователь был восстановлен.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Восстановить пользователя", description = "Восстанавливает пользователя по его идентификатору.")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно восстановлен")
    @PatchMapping("/{id}/recover")
    public ResponseEntity<Void> recoverUser(@PathVariable Long id) {
        userService.recoverUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обрабатывает частичное обновление пользователя.
     * <p>
     * Метод принимает идентификатор пользователя и данные для частичного обновления,
     * переданные в запросе. Только те поля, которые указаны в запросе, будут обновлены.
     * Остальные поля пользователя останутся без изменений.
     *
     * @param id Идентификатор пользователя, который необходимо обновить.
     * @param request Объект {@link PatchUserRequest}, содержащий поля для частичного обновления.
     * @return {@link ResponseEntity} с данными обновленного пользователя в формате {@link CreateUserResponse}.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Частичное обновление пользователя", description = "Обновляет только указанные поля пользователя по его идентификатору.")
    @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены")
    @PatchMapping("/{id}")
    public ResponseEntity<CreateUserResponse> patchUser(@PathVariable Long id, @Valid @RequestBody PatchUserRequest request) {
        CreateUserResponse response = userService.patchUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/replacement")
    public ResponseEntity<Void> replaceLeader(
            @RequestBody LeaderReplacementRequest leaderReplacementRequest,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        userService.replaceLeader(leaderReplacementRequest, currentUser);
        return ResponseEntity.ok().build();
    }
}
