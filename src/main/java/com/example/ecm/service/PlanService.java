package com.example.ecm.service;

import com.example.ecm.dto.requests.CreatePlanRequest;
import com.example.ecm.dto.responses.CreatePlanResponse;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.PlanMapper;
import com.example.ecm.model.Plan;
import com.example.ecm.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    public CreatePlanResponse createPlan(CreatePlanRequest createPlanRequest) {
        return planMapper.toCreatePlanResponse(planRepository.save(planMapper.toPlan(createPlanRequest)));
    }

    public CreatePlanResponse getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Plan", id)));
        return planMapper.toCreatePlanResponse(plan);
    }

    public List<CreatePlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(planMapper::toCreatePlanResponse)
                .toList();
    }

    public CreatePlanResponse updatePlan(Long id, CreatePlanRequest createPlanRequest) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Plan", id)));
        Plan newPlan = planMapper.toPlan(createPlanRequest);
        newPlan.setId(plan.getId());
        return planMapper.toCreatePlanResponse(planRepository.save(newPlan));
    }

    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Plan", id)));
        planRepository.delete(plan);
    }
}

