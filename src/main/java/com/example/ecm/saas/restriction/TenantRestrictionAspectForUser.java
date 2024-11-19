package com.example.ecm.saas.restriction;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Tenant;
import com.example.ecm.repository.TenantRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForUser;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect для проверки доступа к Tenant ресурсам на основе текущего пользователя.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForUser {

    private final TenantRepository tenantRepository;

    /**
     * Проверяет доступ к Tenant.
     *
     * @param joinPoint текущая точка выполнения метода.
     * @param tenantRestrictedForUser аннотация, указывающая на проверку.
     * @return результат выполнения метода, если проверка успешна.
     * @throws Throwable если доступ запрещен или метод генерирует исключение.
     */
    @Around("@annotation(tenantRestrictedForUser)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForUser tenantRestrictedForUser) throws Throwable {
        if (hasAccess(joinPoint.getArgs())) {
            return joinPoint.proceed();
        } else {
            throw new SecurityException("Доступ запрещен: у вас нет прав на этот ресурс.");
        }
    }

    private boolean hasAccess(Object[] args) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new SecurityException("Ошибка авторизации: пользователь не найден.");
        }

        if (userPrincipal.isAdmin()) {
            return true;
        }

        Tenant tenant = tenantRepository.findById(TenantContext.getCurrentTenantId())
                .orElseThrow(() -> new NotFoundException("Тенант не найден."));
        return tenant.getOwner().getId().equals(userPrincipal.getId());
    }
}
