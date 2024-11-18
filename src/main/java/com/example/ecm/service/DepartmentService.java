package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateDepartmentRequest;
import com.example.ecm.dto.responses.CreateDepartmentResponse;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.DepartmentMapper;
import com.example.ecm.model.Department;
import com.example.ecm.model.User;
import com.example.ecm.repository.DepartmentRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Сервис для управления департаментом
 */
@Component
@RequiredArgsConstructor
public class DepartmentService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * Создать департамент
     */
    public CreateDepartmentResponse createDepartment(CreateDepartmentRequest request) {
        User leader = userRepository.findById(request.getLeaderId())
                .orElseThrow(() -> new NotFoundException("User with id: " + request.getLeaderId() + " not found"));

        Department department = departmentMapper.toDepartment(request, leader);

        if(request.parentId != null){
            Department departmentParent = departmentRepository.findById(request.parentId)
                    .filter(Department::getIsAlive)
                    .orElseThrow(() -> new NotFoundException("Department with id: " + request.parentId + " not found"));
            if(!departmentParent.getUsers().contains(leader))
                throw new ForbiddenException("User with id: " + request.getLeaderId() + " is not part of this department");
            departmentParent.getChildren().add(department);

            }
        department = departmentRepository.save(department);
        return departmentMapper.toCreateDepartmentResponse(department);
    }

    /**
     * Получить департамент по идентификатору
     * @param id идентификатор департамента
     * @return DTO департамента
     */
    public CreateDepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .filter(Department::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Department with id: " + id + " not found"));

        return departmentMapper.toCreateDepartmentResponse(department);
    }

    /**
     * Делает департамент неактивным по идентификатору
     * @param id идентификатор департамента
     * @param userPrincipal пользователь, выполняющий действие
     */
    public void deleteDepartmentById(Long id, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(id)
                .filter(Department::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Department with id: " + id + " not found"));

        if(!userPrincipal.isAdmin()||!userPrincipal.getId().equals(department.getLeader().getId()))
            throw new ForbiddenException("You do not have permission to delete this department");

        department.setIsAlive(false);
        departmentRepository.save(department);
    }

    /**
     * Восстанавливает департамент по идентификатору
     * @param id идентификатор департамента
     * @param userPrincipal пользователь, выполняющий действие
     */
    public void recoverDepartmentById(Long id, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deleted department with id: " + id + " not found"));

        if(!userPrincipal.isAdmin()||!userPrincipal.getId().equals(department.getLeader().getId()))
            throw new ForbiddenException("You do not have permission to recover this department");

        department.setIsAlive(true);
        departmentRepository.save(department);
    }

    /**
     * Обновить департамент
     */
    public CreateDepartmentResponse updateDepartment(Long id, CreateDepartmentRequest request, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(id)
                .filter(Department::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Department with id: " + id + " not found"));

        if(!userPrincipal.isAdmin()||!userPrincipal.getId().equals(department.getLeader().getId()))
            throw new ForbiddenException("You do not have permission to update this department");

        User leader = userRepository.findById(request.getLeaderId())
                .orElseThrow(() -> new NotFoundException("User with id: " + request.getLeaderId() + " not found"));

        department.setName(request.getName());
        department.setLeader(leader);
        department = departmentRepository.save(department);

        return departmentMapper.toCreateDepartmentResponse(department);
    }

    /**
     * Добавление участников в департамент
     * @param id идентификатор департамента
     * @param membersId идентификаторы участников, которых нужно добавить
     * @param userPrincipal пользователь, выполняющий действие
     * @return DTO обновленного департамента
     */
    public CreateDepartmentResponse addMembers(Long id, List<Long> membersId, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(id)
                .filter(Department::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Department with id: " + id + " not found"));

        if(!userPrincipal.isAdmin()||!userPrincipal.getId().equals(department.getLeader().getId()))
            throw new ForbiddenException("You do not have permission to add members to this department");

        List<User> members = membersId.stream()
                .map(memberId -> userRepository.findById(memberId)
                        .orElseThrow(() -> new NotFoundException("User with id: " + memberId + " not found")))
                .toList();

        department.getUsers().addAll(members);
        department = departmentRepository.save(department);
        return departmentMapper.toCreateDepartmentResponse(department);
    }

    /**
     * Исключение участников из департамента
     * @param id идентификатор департамента
     * @param membersId идентификаторы участников, которых нужно исключить
     * @param userPrincipal пользователь, выполняющий действие
     * @return DTO обновленного департамента
     */
    public CreateDepartmentResponse deleteMembers(Long id, List<Long> membersId, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(id)
                .filter(Department::getIsAlive)
                .orElseThrow(() -> new NotFoundException("Department with id: " + id + " not found"));

        if(!userPrincipal.isAdmin()||!userPrincipal.getId().equals(department.getLeader().getId()))
            throw new ForbiddenException("You do not have permission to delete members from this department");

        List<User> membersToRemove = membersId.stream()
                .map(memberId -> userRepository.findById(memberId)
                        .orElseThrow(() -> new NotFoundException("User with id: " + memberId + " not found")))
                .filter(department.getUsers()::contains)
                .toList();

        if (membersToRemove.isEmpty()) {
            throw new NotFoundException("No members from the provided list belong to the department with id: " + id);
        }
        department.getUsers().removeAll(membersToRemove);
        department = departmentRepository.save(department);
        return departmentMapper.toCreateDepartmentResponse(department);
    }

    public boolean isMemberInAnyDepartmentOfLeader(User member, User leader) {

        List<Department> departments = departmentRepository.findByLeaderId(leader.getId());
        if (departments.isEmpty()) {
            throw new NotFoundException("User with id: " + leader.getId() + " don't have any departments");
        }

        return departments.stream()
                .anyMatch(department -> department.getUsers().contains(member));
    }
}
