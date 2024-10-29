package com.example.ecm.mapper;

import com.example.ecm.dto.requests.CreateUserRequest;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.dto.responses.GetRoleResponse;
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
        user.setIsAlive(true);
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
    public Set<GetRoleResponse> toRolesResponse(Set<Role> roles) {
        Set<GetRoleResponse> rolesResponse = new HashSet<>();
        for(Role role : roles) {
            GetRoleResponse getRoleResponse = new GetRoleResponse();
            getRoleResponse.setId(role.getId());
            getRoleResponse.setName(role.getName());
            rolesResponse.add(getRoleResponse);
        }
        return rolesResponse;
    }
}