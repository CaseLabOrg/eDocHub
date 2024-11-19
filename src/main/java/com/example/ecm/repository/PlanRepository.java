package com.example.ecm.repository;

import com.example.ecm.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findFirstByMaxUsersGreaterThanOrderByMaxUsersAsc(Long maxUsers);
}
