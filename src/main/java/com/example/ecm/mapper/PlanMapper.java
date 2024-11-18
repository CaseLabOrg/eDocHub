package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreatePlanRequest;
import com.example.ecm.dto.responses.CreatePlanResponse;
import com.example.ecm.model.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {
    public Plan toPlan(CreatePlanRequest request) {
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setMaxUsers(request.getMaxUsers());
        return plan;
    }

    public CreatePlanResponse toCreatePlanResponse(Plan plan) {
        CreatePlanResponse response = new CreatePlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setPrice(plan.getPrice());
        response.setMaxUsers(plan.getMaxUsers());
        return response;
    }
}

