package com.example.ecm.saas;

import com.example.ecm.model.DocumentType;
import com.example.ecm.repository.DocumentTypeRepository;
import com.example.ecm.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import java.util.List;



@Aspect
@Component
@RequiredArgsConstructor
public class TenantRestrictionAspectForDocumentType {
    private final DocumentTypeService documentTypeService;
    private final DocumentTypeRepository documentTypeRepository;

    @Around("@annotation(tenantRestricted)")
    public Object checkTenantAccess(ProceedingJoinPoint joinPoint, TenantRestricted tenantRestricted) throws Throwable {

        if (hasAccess(joinPoint)) {
            return joinPoint.proceed();
        } else {
            throw new SecurityException("У вас нет доступа к этому ресурсу");
        }
    }


    private boolean hasAccess(ProceedingJoinPoint joinPoint) {
        Long resourceTenantId = getResourceTenantId(joinPoint.getArgs());
        return TenantContext.getCurrentTenantId().equals(resourceTenantId);
    }

    private Long getResourceTenantId(Object[] args) {
        if (args.length > 0 && args[0] instanceof Long id) {
            DocumentType documentType = documentTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("DocumentType не найден для ID: " + id));
            if (documentType.getTenant() != null) {
                return documentType.getTenant().getId();
            } else {
                throw new IllegalArgumentException("Tenant не найден для DocumentType с ID: " + id);
            }
        }
        else if( args[0] instanceof Boolean) {
            List<DocumentType> documentTypes = documentTypeRepository.findAll();
            if(documentTypes.size() > 0){
                return documentTypes.get(0).getTenant().getId();
            } else {
                throw new IllegalArgumentException("DocumentType не существует в базе данных ");
            }

        }
        throw new IllegalArgumentException("Ничего не выйдет");
    }


}