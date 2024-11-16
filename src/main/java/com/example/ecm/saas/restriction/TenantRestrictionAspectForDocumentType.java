package com.example.ecm.saas.restriction;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.DocumentTypeRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForDocumentType;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect для проверки доступа к ресурсам DocumentType на основе текущего Tenant.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForDocumentType {

    private final DocumentTypeRepository documentTypeRepository;

    /**
     * Проверяет доступ к ресурсу DocumentType.
     *
     * @param joinPoint      текущая точка выполнения метода.
     * @param tenantRestricted аннотация, указывающая на проверку.
     * @return результат выполнения метода, если проверка успешна.
     * @throws Throwable если доступ запрещен или метод генерирует исключение.
     */
    @Around("@annotation(tenantRestricted)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForDocumentType tenantRestricted) throws Throwable {
        if (hasAccess(joinPoint)) {
            return joinPoint.proceed();
        } else {
            throw new SecurityException("Доступ запрещен: у вас нет прав на этот ресурс.");
        }
    }

    private boolean hasAccess(ProceedingJoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new SecurityException("Ошибка авторизации: пользователь не найден.");
        }

        if (userPrincipal.isAdmin()) {
            return true;
        }

        Long resourceTenantId = getResourceTenantId(joinPoint.getArgs());
        return TenantContext.getCurrentTenantId().equals(resourceTenantId);
    }

    private Long getResourceTenantId(Object[] args) {
        if (args.length > 0 && args[0] instanceof Long id) {
            DocumentType documentType = documentTypeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("DocumentType не найден для ID: " + id));
            if (documentType.getTenant() != null) {
                return documentType.getTenant().getId();
            } else {
                throw new NotFoundException("У Организации нет такого типа документов");
            }
        }
        throw new IllegalArgumentException("Некорректный аргумент: ID DocumentType отсутствует.");
    }
}
