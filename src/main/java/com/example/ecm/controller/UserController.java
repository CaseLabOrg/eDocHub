package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.CreateUserRequest;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.dto.requests.PutRoleRequest;
import com.example.ecm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Loggable
public class UserController {

    private final UserService userService;

    /**
     * Создание нового пользователя.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя
     * @return DTO с данными созданного пользователя
     */
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        CreateUserResponse userResponse = userService.createUser(createUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Получение пользователя по его ID с возвратом данных в виде DTO.
     *
     * @param id Идентификатор пользователя
     * @return DTO с данными пользователя, если найден, или 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreateUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Получение списка всех пользователей с возвратом данных в виде DTO.
     *
     * @return Список DTO с данными всех пользователей
     */
    @GetMapping
    public ResponseEntity<List<CreateUserResponse>> getAllUsers() {
        List<CreateUserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> addRole(@PathVariable Long id, @Valid @RequestBody PutRoleRequest role) {
        return ResponseEntity.ok(userService.addRole(id, role));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> removeRole(@PathVariable Long id, @Valid @RequestBody PutRoleRequest role) {
        return ResponseEntity.ok(userService.removeRole(id, role));
    }

    /**
     * Обновление данных пользователя по его ID на основе DTO.
     *
     * @param id                Идентификатор пользователя
     * @param updateUserRequest DTO с обновленными данными пользователя
     * @return Обновленный пользователь в виде DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<CreateUserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest updateUserRequest) {
        CreateUserResponse updatedUser = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Ответ без содержимого (204 No Content), если пользователь был удален
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
