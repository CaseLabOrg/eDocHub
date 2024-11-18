package com.example.ecm.mapper;

import com.example.ecm.dto.responses.CreateSubscriptionResponse;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.Plan;
import com.example.ecm.model.Subscription;
import com.example.ecm.model.Tenant;
import com.example.ecm.model.User;
import com.example.ecm.repository.PlanRepository;
import com.example.ecm.repository.TenantRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.saas.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {
    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final UserMapper userMapper;
    private final PlanMapper planMapper;

    public Subscription toSubscription(Long planId) {
        Subscription subscription = new Subscription();
        Tenant tenant = tenantRepository.findById(TenantContext.getCurrentTenantId())
                .orElseThrow(() ->  new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Tenant", TenantContext.getCurrentTenantId())));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() ->  new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Plan", planId)));
        tenant.setSubscription(subscription);
        subscription.setTenant(tenant);
        subscription.setPlan(plan);
        return subscription;
    }

    public CreateSubscriptionResponse toCreateSubscriptionResponse(Subscription subscription) {
        CreateSubscriptionResponse response = new CreateSubscriptionResponse();
        response.setId(subscription.getId());
        response.setTenant_id(subscription.getTenant().getId());
        response.setPlan(planMapper.toCreatePlanResponse(subscription.getPlan()));
        response.setStatus(subscription.getStatus().name());
        response.setStartDate(subscription.getStartDate());
        response.setEndDate(subscription.getEndDate());
        return response;
    }
}

