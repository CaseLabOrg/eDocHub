package com.example.ecm.security;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.User;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.saas.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для загрузки данных пользователя по имени пользователя (email).
 *
 * <p>Этот класс:
 * <ul>
 *     <li>Находит пользователя в базе данных по email.</li>
 *     <li>Создаёт объект {@link UserPrincipal}, который реализует интерфейс {@link UserDetails}.</li>
 *     <li>Устанавливает объект {@link Authentication} в {@link SecurityContextHolder}.</li>
 *     <li>Проверяет активность организации, связанной с пользователем, и устанавливает текущий идентификатор арендатора
 *         в {@link TenantContext}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository; // Репозиторий для работы с пользователями.

    /**
     * Загружает данные пользователя по его имени пользователя (email).
     *
     * @param username email пользователя.
     * @return объект {@link UserDetails}, представляющий пользователя.
     * @throws UsernameNotFoundException если пользователь с указанным email не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Поиск пользователя по email.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + username + " не найден"));

        // Преобразование ролей пользователя в список SimpleGrantedAuthority.
        List<SimpleGrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        // Создание объекта UserPrincipal.
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .login(user.getEmail())
                .authorities(roles)
                .password(user.getPassword())
                .build();

        // Установка объекта Authentication в SecurityContextHolder.
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()));

        // Проверка активности организации и установка TenantContext.
        if (user.getTenant().getIsAlive()) {
            TenantContext.setCurrentTenant(user.getTenant().getId());
        } else {
            throw new NotFoundException("Организация не найдена"); // Исключение, если организация не активна.
        }

        return userPrincipal; // Возврат объекта UserPrincipal.
    }
}
