package com.example.ecm.service;

import com.example.ecm.dto.patch_requests.PatchUserRequest;
import com.example.ecm.dto.requests.CreateUserRequest;
import com.example.ecm.dto.requests.LeaderReplacementRequest;
import com.example.ecm.dto.responses.CreateUserResponse;
import com.example.ecm.dto.requests.PutRoleRequest;
import com.example.ecm.enums.ExceptionMessage;
import com.example.ecm.exception.ForbiddenException;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.mapper.UserMapper;
import com.example.ecm.model.*;
import com.example.ecm.repository.PlanRepository;
import com.example.ecm.repository.RoleRepository;
import com.example.ecm.repository.TenantRepository;
import com.example.ecm.repository.UserReplacementsRepository;
import com.example.ecm.repository.UserRepository;
import com.example.ecm.saas.TenantContext;
import com.example.ecm.saas.annotation.TenantRestrictedForUser;
import com.example.ecm.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Сервис для выполнения операций с пользователями, включая создание, получение,
 * обновление и удаление пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final DepartmentService departmentService;
    private final UserReplacementsRepository userReplacementsRepository;

    /**
     * Создание нового пользователя на основе данных из DTO.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя
     * @return DTO с данными сохраненного пользователя
     */
    @TenantRestrictedForUser
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        checkPlan();

        User user = userMapper.toUser(createUserRequest);
        user.setTenant(tenantRepository.findById(TenantContext.getCurrentTenantId()).orElseThrow(() -> new NotFoundException("Tenant not found")));
        user.setRoles(Set.of(roleRepository.findByName("USER").orElseThrow(() -> new NotFoundException("Role with name: USER not found"))));
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);

        return userMapper.toCreateUserResponse(savedUser);
    }



    @Transactional
    public CreateUserResponse createUserAdmin(CreateUserRequest createUserRequest) {
        checkPlan();
        User user = userMapper.toUser(createUserRequest);

        user.setTenant(tenantRepository.findById(TenantContext.getCurrentTenantId()).orElseThrow(() -> new NotFoundException("Tenant not found")));

        user.setRoles(Set.of(roleRepository.findByName("ADMIN").orElseThrow(() -> new NotFoundException("Role with name: ADMIN not found"))));
        user.setPassword(encoder.encode(createUserRequest.getPassword()));
        User savedUser = userRepository.save(user);


        return userMapper.toCreateUserResponse(savedUser);
    }

    @Transactional
    public CreateUserResponse createUserOwner(CreateUserRequest createUserRequest, Long tenantId) {
        checkPlan();
        User user = userMapper.toUser(createUserRequest);

        user.setTenant(tenantRepository.findById(tenantId).orElseThrow(() -> new NotFoundException("Tenant not found")));

        user.setRoles(Set.of(
                roleRepository.findByName("OWNER").orElseThrow(() -> new NotFoundException("Role with name: OWNER not found")),
                roleRepository.findByName("ADMIN").orElseThrow(() -> new NotFoundException("Role with name: ADMIN not found"))
        ));


        user.setPassword(encoder.encode(createUserRequest.getPassword()));

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new NotFoundException("Tenant not found"));
        tenant.setOwner(user);

        User savedUser = userRepository.save(user);

        return userMapper.toCreateUserResponse(savedUser);
    }


    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Optional с объектом пользователя, если найден
     */
    @TenantRestrictedForUser
    public CreateUserResponse getUserById(Long id, Boolean showOnlyALive) {

        Optional<User> attribute = userRepository.findById(id);

        if (showOnlyALive) {
            attribute = attribute.filter(User::getIsAlive);
        }

        return attribute
                .map(userMapper::toCreateUserResponse)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
    }


    @TenantRestrictedForUser
    public CreateUserResponse addRole(Long id, PutRoleRequest request) {
        Role role = roleRepository.findByName(request.getName().toUpperCase()).orElseThrow(() -> new NotFoundException("Role with name: " + request.getName().toUpperCase() + " not found"));
        User user = userRepository.findById(id)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.getRoles().add(role);
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    @TenantRestrictedForUser
    public CreateUserResponse removeRole(Long id, PutRoleRequest request) {
        Role role = roleRepository.findByName(request.getName().toUpperCase()).orElseThrow(() -> new NotFoundException("Role with name: " + request.getName().toUpperCase() + " not found"));
        User user = userRepository.findById(id)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.getRoles().remove(role);
        return userMapper.toCreateUserResponse(userRepository.save(user));
    }

    /**
     * Получение списка всех пользователей с преобразованием в DTO.
     *
     * @return Список DTO с данными пользователей
     */
    @TenantRestrictedForUser
    public List<CreateUserResponse> getAllUsers(Boolean showOnlyALive, UserPrincipal userPrincipal) {
        Stream<User> userStream = userRepository.findAll().stream();

        if (showOnlyALive) {
            userStream = userStream.filter(User::getIsAlive);
        }
        if(!userPrincipal.isAdmin()) {
            userStream = userStream.filter(user -> user.getTenant().getId().equals(TenantContext.getCurrentTenantId()));
        }
        return userStream
                .map(userMapper::toCreateUserResponse)
                .toList();
    }

    /**
     * Обновление существующего пользователя по ID на основе данных из DTO.
     *
     * @param id                Идентификатор пользователя
     * @param updateUserRequest DTO с обновленными данными пользователя
     * @return Обновленный объект пользователя в виде DTO
     */
    @TenantRestrictedForUser
    public CreateUserResponse updateUser(Long id, CreateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.setName(updateUserRequest.getName());
        user.setSurname(updateUserRequest.getSurname());
        user.setEmail(updateUserRequest.getEmail());
        user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        User updatedUser = userRepository.save(user);

        return userMapper.toCreateUserResponse(updatedUser);
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     */
    @TenantRestrictedForUser
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        user.setIsAlive(false);
        userRepository.save(user);
    }

    @TenantRestrictedForUser
    public void recoverUser(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> !u.getIsAlive())
                .orElseThrow(() -> new NotFoundException("Deleted User with id: " + id + " not found"));
        user.setIsAlive(true);
        userRepository.save(user);
    }

    /**
     * Поиск пользователя по email
     *
     * @param email Почта пользователя
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Частичное обновление пользователя
     *
     * @param id      идентификатор пользователя
     * @param request запрос на частичное обновление пользователя
     * @return ответ с данными обновленного пользователя
     */
    @TenantRestrictedForUser
    public CreateUserResponse patchUser(Long id, PatchUserRequest request) {
        User user = userRepository.findById(id)
                .filter(User::getIsAlive)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " not found"));
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getSurname() != null) {
            user.setSurname(request.getSurname());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toCreateUserResponse(updatedUser);
    }

    public void replaceLeader(LeaderReplacementRequest leaderReplacementRequest, UserPrincipal currentUser) {
        Long successorId = leaderReplacementRequest.getSuccessorId();

        User successor = userRepository.findById(successorId)
                .orElseThrow(() -> new NotFoundException("User with id: " + successorId + " not found"));
        User leader = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + currentUser.getId() + " not found"));
        if(!departmentService.isMemberInAnyDepartmentOfLeader(successor, leader))
            throw new ForbiddenException("User with id: " + successor.getId() + " is not a member of any department of the leader with id: " + leader.getId());

        UserReplacement leaderReplacement = new UserReplacement();
        leaderReplacement.setSuccessor(successor);
        leaderReplacement.setPredecessor(leader);
        leaderReplacement.setUntil(leaderReplacementRequest.getUntil());

        userReplacementsRepository.save(leaderReplacement);
    }

    private void checkPlan() {
        Tenant tenant = tenantRepository.findById(TenantContext.getCurrentTenantId())
                .orElseThrow(() -> new ForbiddenException(ExceptionMessage.ENTITY_NOT_FOUND.generateNotFoundEntityMessage("Tenant", TenantContext.getCurrentTenantId())));
        if(tenant.getUsers().size() == tenant.getSubscription().getPlan().getMaxUsers()) {
            Plan newPlan = planRepository.findFirstByMaxUsersGreaterThanOrderByMaxUsersAsc(tenant.getSubscription().getPlan().getMaxUsers())
                    .orElseThrow(() -> new ForbiddenException(ExceptionMessage.USERS_LIMIT_REACHED.getMessage()));
            Subscription subscription = tenant.getSubscription();
            subscription.setPlan(newPlan);
            tenant.setSubscription(subscription);
            tenantRepository.save(tenant);
        }
    }


}

