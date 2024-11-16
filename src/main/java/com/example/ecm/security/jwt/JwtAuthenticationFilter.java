package com.example.ecm.security.jwt;

import com.example.ecm.saas.TenantContext;
import com.example.ecm.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.example.ecm.security.UserPrincipalAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Фильтр аутентификации, который обрабатывает JWT-токены в HTTP-запросах.
 *
 * <p>Этот фильтр:
 * <ul>
 *     <li>Извлекает JWT-токен из заголовка запроса "Authorization".</li>
 *     <li>Декодирует токен и конвертирует его в объект {@link UserPrincipal}.</li>
 *     <li>Устанавливает объект {@link Authentication} в контекст безопасности Spring.</li>
 *     <li>Очищает {@link TenantContext} после завершения обработки запроса.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtDecoder decoder; // Компонент для декодирования JWT-токенов.
    private final JwtToPrincipalConverter converter; // Конвертер для преобразования декодированного токена в UserPrincipal.

    /**
     * Основной метод фильтра, выполняющий обработку каждого запроса.
     *
     * @param request     текущий HTTP-запрос.
     * @param response    текущий HTTP-ответ.
     * @param filterChain цепочка фильтров для обработки запроса.
     * @throws ServletException если возникает ошибка обработки запроса.
     * @throws IOException      если возникает ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Извлечение токена, его декодирование и установка в SecurityContext.
        extractToken(request)
                .map(decoder::decode)
                .map(converter::convert)
                .map(UserPrincipalAuthenticationToken::new)
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));

        try {
            // Продолжение цепочки фильтров.
            filterChain.doFilter(request, response);
        } finally {
            // Очистка TenantContext для предотвращения утечек данных между запросами.
            TenantContext.clear();
        }
    }

    /**
     * Извлекает JWT-токен из заголовка "Authorization" HTTP-запроса.
     *
     * @param request HTTP-запрос.
     * @return {@link Optional}, содержащий токен, если он существует и корректно начинается с "Bearer".
     */
    private Optional<String> extractToken(HttpServletRequest request) {
        var token = request.getHeader("Authorization"); // Получение заголовка Authorization.
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return Optional.of(token.substring(7)); // Удаление префикса "Bearer " и возврат токена.
        }
        return Optional.empty(); // Если токен отсутствует или некорректен.
    }
}
