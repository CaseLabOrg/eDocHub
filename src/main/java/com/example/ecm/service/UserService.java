package com.example.ecm.service;

import com.example.ecm.dto.CreateUserRequest;
import com.example.ecm.dto.CreateUserResponse;
import com.example.ecm.mapper.UserMapper;
import com.example.ecm.model.Role;
import com.example.ecm.model.User;
import com.example.ecm.repository.RoleRepository;
import com.example.ecm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для выполнения операций с пользователями, включая создание, получение,
 * обновление и удаление пользователей.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    /**
     * Конструктор для внедрения зависимости UserRepository и UserMapper.
     *
     * @param userRepository Репозиторий для работы с пользователями
     * @param userMapper     Маппер для преобразования между User и DTO
     */
    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    /**
     * Создание нового пользователя на основе данных из DTO.
     *
     * @param createUserRequest DTO с данными для создания нового пользователя
     * @return DTO с данными сохраненного пользователя
     */
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.toUser(createUserRequest);
        user.setRoles(getSavedRoles(user));
        User savedUser = userRepository.save(user);


        return userMapper.toCreateUserResponse(savedUser);
    }

    /**
     * Получение пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     * @return Optional с объектом пользователя, если найден
     */
    public Optional<CreateUserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toCreateUserResponse);
    }

    /**
     * Получение списка всех пользователей с преобразованием в DTO.
     *
     * @return Список DTO с данными пользователей
     */
    public List<CreateUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toCreateUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Обновление существующего пользователя по ID на основе данных из DTO.
     *
     * @param id                Идентификатор пользователя
     * @param updateUserRequest DTO с обновленными данными пользователя
     * @return Обновленный объект пользователя в виде DTO
     */
    public CreateUserResponse updateUser(Long id, CreateUserRequest updateUserRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(updateUserRequest.getName());
                    existingUser.setSurname(updateUserRequest.getSurname());
                    existingUser.setEmail(updateUserRequest.getEmail());
                    existingUser.setPassword(updateUserRequest.getPassword());
                    existingUser.setRoles(userMapper.toRoles(updateUserRequest.getRoles()));
                    User updatedUser = userRepository.save(existingUser);
                    return userMapper.toCreateUserResponse(updatedUser);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Удаление пользователя по его ID.
     *
     * @param id Идентификатор пользователя
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Поиск пользователя по email
     *
     * @param email Почта пользователя
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Возвращает набор ролей, связанных с пользователем, сохраняя новые или обновляя существующие.
     *
     * @param user Пользователь, для которого определяются роли
     * @return Набор сохраненных ролей
     */
    private Set<Role> getSavedRoles(User user){
        Set<Role> savedRoles = new HashSet<>();
        for(Role role : user.getRoles()){
            Optional<Role> existingRole = roleRepository.findByName(role.getName());
            if(existingRole.isPresent()){
                role = existingRole.get();
            }
            role.getUsers().add(user);
            savedRoles.add(roleRepository.save(role));
        }

        return savedRoles;
    }
}

