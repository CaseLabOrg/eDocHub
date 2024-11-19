package com.example.ecm.controller;

import com.example.ecm.aop.Loggable;
import com.example.ecm.dto.requests.LoginRequest;
import com.example.ecm.dto.responses.LoginResponse;
import com.example.ecm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления аутентификацией пользователей.
 * Предоставляет конечные точки для входа в систему.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Loggable
@Tag(name = "Auth Controller", description = "Управление аутентификацией пользователей")
public class AuthController {

    private final AuthService serviceAuth;

    /**
     * POST-метод для входа в систему.
     *
     * @param request Объект запроса с данными для входа (электронная почта и пароль).
     * @return Ответ с данными пользователя после успешной аутентификации.
     */
    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Позволяет пользователю войти в систему, предоставив email и пароль")
    public LoginResponse login(
            @Valid @RequestBody @Parameter(description = "Данные для входа в систему") LoginRequest request) {
        return serviceAuth.attemptLogin(request.getEmail(), request.getPassword());
    }
}
