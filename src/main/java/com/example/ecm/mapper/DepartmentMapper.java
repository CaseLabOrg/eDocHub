package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateDepartmentRequest;
import com.example.ecm.dto.responses.CreateDepartmentResponse;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.model.Department;
import com.example.ecm.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Преобразование DTO объектов департаментов в сущности и наоборот.
 */
@Component
public class DepartmentMapper {

    private final UserMapper userMapper;

    public DepartmentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Преобразует объект CreateDepartmentRequest в сущность Department.
     *
     * @param request объект запроса на создание департамента
     * @param leader  сущность пользователя - лидер департамента
     * @return объект сущности Department, содержащий данные из запроса
     */
    public Department toDepartment(CreateDepartmentRequest request, User leader) {
        Department department = new Department();
        department.setName(request.getName());
        department.setLeader(leader);
        department.setIsAlive(true);
        return department;
    }

    /**
     * Преобразует сущность Department в объект CreateDepartmentResponse.
     *
     * @param department сущность департамента
     * @return DTO, содержащий данные департамента
     */
    public CreateDepartmentResponse toCreateDepartmentResponse(Department department) {
        CreateDepartmentResponse response = new CreateDepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setLeader(userMapper.toCreateUserResponse(department.getLeader()));
        response.setMembers(toCreateUserResponses(department.getUsers()));
        return response;
    }

    /**
     * Преобразует список сущностей User в список DTO CreateUserResponse.
     *
     * @param users список сущностей пользователей
     * @return список DTO с данными пользователей
     */
    private List<CreateUserResponse> toCreateUserResponses(List<User> users) {
        return users.stream()
                .map(userMapper::toCreateUserResponse)
                .collect(Collectors.toList());
    }
}
