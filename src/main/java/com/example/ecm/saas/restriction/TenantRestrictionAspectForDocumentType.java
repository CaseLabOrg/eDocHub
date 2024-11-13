package com.example.ecm.saas.restriction;

import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.DocumentTypeRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForDocumentType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForDocumentType {

    private final DocumentTypeRepository documentTypeRepository;

    @Around("@annotation(tenantRestricted)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForDocumentType tenantRestricted) throws Throwable {

        if (hasAccess(joinPoint)) {
            return joinPoint.proceed();
        } else {
            throw new NotFoundException("У вас нет доступа к этому ресурсу");
        }
    }


    private boolean hasAccess(ProceedingJoinPoint joinPoint) {
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
                throw new NotFoundException("Tenant не найден для DocumentType с ID: " + id);
            }

        }
        throw new NotFoundException("Ничего не выйдет");
    }


}