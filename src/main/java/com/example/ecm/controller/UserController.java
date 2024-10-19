package com.example.ecm.controller;

import com.example.ecm.dto.CreateUserRequest;
import com.example.ecm.dto.CreateUserResponse;
import com.example.ecm.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
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

    @PutMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> addRole(@PathVariable Long id, @NotNull(message = "role cannot be null") @RequestBody String roleName) {
        return ResponseEntity.ok(userService.addRole(id, roleName));
    }

    @DeleteMapping("/{id}/role")
    public ResponseEntity<CreateUserResponse> removeRole(@PathVariable Long id, @NotNull(message = "role cannot be null") @RequestBody String roleName) {
        return ResponseEntity.ok(userService.removeRole(id, roleName));
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
