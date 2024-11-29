package com.example.ecm.saas.restriction;

import com.example.ecm.exception.AuthException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Attribute;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForAttribute;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect для проверки доступа к ресурсам Attribute на основе текущего Tenant.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForAttribute {

    private final AttributeRepository attributeRepository;

    /**
     * Проверяет доступ к ресурсу Attribute.
     *
     * @param joinPoint                 текущая точка выполнения метода.
     * @param tenantRestrictedForAttribute аннотация, указывающая на проверку.
     * @return результат выполнения метода, если проверка успешна.
     * @throws Throwable если доступ запрещен или метод генерирует исключение.
     */
    @Around("@annotation(tenantRestrictedForAttribute)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForAttribute tenantRestrictedForAttribute) throws Throwable {
        if (hasAccess(joinPoint)) {
            return joinPoint.proceed();
        } else {
            throw new NotFoundException("Attribute with given id is not found");
        }
    }

    private boolean hasAccess(ProceedingJoinPoint joinPoint) {
        Long resourceTenantId = getResourceTenantId(joinPoint.getArgs());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new AuthException("Bad credentials");
        }

        if (userPrincipal.isAdmin()) {
            return true;
        }

        return TenantContext.getCurrentTenantId().equals(resourceTenantId);
    }

    private Long getResourceTenantId(Object[] args) {
        if (args.length > 0 && args[0] instanceof Long id) {
            Attribute attribute = attributeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Attribute with given id is not found"));
            if (attribute.getTenant() != null) {
                return attribute.getTenant().getId();
            } else {
                throw new NotFoundException("Attribute with given id is not found");
            }
        }
        throw new IllegalArgumentException("Некорректный аргумент: ID атрибута отсутствует.");
    }
}
