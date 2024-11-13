package com.example.ecm.saas.restriction;


import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Attribute;
import com.example.ecm.repository.AttributeRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForAttribute;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForAttribute {

    private final AttributeRepository attributeRepository;

    @Around("@annotation(tenantRestrictedForAttribute)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForAttribute tenantRestrictedForAttribute) throws Throwable {

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
            Attribute attribute = attributeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("DocumentType не найден для ID: " + id));
            if (attribute.getTenant() != null) {
                return attribute.getTenant().getId();
            } else {
                throw new NotFoundException("Tenant не найден для DocumentType с ID: " + id);
            }

        }
        throw new NotFoundException("Ничего не выйдет");
    }

}