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

import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForUser {

    private final TenantRepository tenantRepository ;

    @Around("@annotation(tenantRestrictedForUser)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestrictedForUser tenantRestrictedForUser) throws Throwable {

        if (hasAccess(joinPoint.getArgs())) {
            return joinPoint.proceed();
        } else {
            throw new NotFoundException("У вас нет доступа к этому ресурсу");
        }
    }



    private boolean hasAccess(Object[] args) {
        UserPrincipal userPrincipal = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof UserPrincipal) {
                userPrincipal = (UserPrincipal) args[i];
                break;
            }
        }

        if (userPrincipal != null) {
            Tenant tenant = tenantRepository.findById(TenantContext.getCurrentTenantId()).orElseThrow( () -> new NotFoundException("Tenant not found"));
            if (tenant != null) {
                return userPrincipal.getId().equals(tenant.getOwner().getId());
            }
            throw new NotFoundException("Tenant не найден.");
        }
        throw new NotFoundException("Ничего не выйдет");
    }


}