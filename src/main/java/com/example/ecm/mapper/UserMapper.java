package com.example.ecm.mapper;

import com.example.ecm.dto.*;
import com.example.ecm.model.Role;
import com.example.ecm.model.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Компонент, отвечающий за преобразование DTO объектов в сущности и наоборот.
 * Используется для маппинга данных между слоями приложения.
 */
@Component
public class UserMapper {

    /**
     * Преобразует объект CreateUserRequest в сущность User.
     *
     * @param request объект запроса на создание пользователя
     * @return объект сущности User, содержащий данные из запроса
     */
    public User toUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRoles(toRoles(request.getRoles()));
        return user;
    }

    /**
     * Преобразует сущность User в объект CreateUserResponse.
     *
     * @param user сущность пользователя
     * @return DTO, содержащий данные пользователя
     */
    public CreateUserResponse toCreateUserResponse(User user) {
        CreateUserResponse response = new CreateUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setSurname(user.getSurname());
        response.setEmail(user.getEmail());
        response.setRoles(toRolesResponse(user.getRoles()));
        return response;
    }

    /**
     * Преобразует набор сущностей Role в набор DTO RoleResponse.
     *
     * @param roles набор ролей
     * @return набор DTO с данными ролей
     */
    public Set<RoleResponse> toRolesResponse(Set<Role> roles) {
        Set<RoleResponse> rolesResponse = new HashSet<>();
        for(Role role : roles) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());
            rolesResponse.add(roleResponse);
        }
        return rolesResponse;
    }

    /**
     * Преобразует набор DTO RoleRequest в сущности Role.
     *
     * @param roleRequests набор запросов на создание ролей
     * @return набор сущностей ролей
     */
    public Set<Role> toRoles(Set<RoleRequest> roleRequests) {
        Set<Role> roles = new HashSet<>();
        for(RoleRequest roleRequest : roleRequests) {
            Role role = new Role();
            role.setName(roleRequest.getRoleName());
            roles.add(role);
        }

        return roles;
    }
}