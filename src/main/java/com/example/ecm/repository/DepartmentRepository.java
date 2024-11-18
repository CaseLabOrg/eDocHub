package com.example.ecm.repository;

import com.example.ecm.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    /**
     * Получение всех департаментов, где пользователь является лидером.
     *
     * @param leaderId идентификатор пользователя-лидера
     * @return список департаментов
     */
    @Query("SELECT d FROM Department d WHERE d.leader.id = :leaderId AND d.isAlive = true")
    List<Department> findByLeaderId(@Param("leaderId") Long leaderId);
}
