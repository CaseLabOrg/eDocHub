package com.example.ecm.controller;

import com.example.ecm.model.User;
import com.example.ecm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления пользователями.
 * Предоставляет CRUD API для взаимодействия с сущностью User.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Конструктор для внедрения зависимости UserService.
     *
     * @param userService Сервис для работы с пользователями
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создание нового пользователя.
     *
     * @param user Данные нового пользователя
     * @return Созданный пользователь
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Пользователь с указанным ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение всех пользователей.
     *
     * @return Список всех пользователей
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Обновление пользователя по его ID.
     *
     * @param id          Идентификатор пользователя
     * @param updatedUser Объект с обновленными данными пользователя
     * @return Обновленный пользователь
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Ответ без содержимого, если пользователь был удален
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
